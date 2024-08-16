from flask import Flask, request, jsonify
import os
import json
import queue
import threading
import time
from langchain.embeddings import CacheBackedEmbeddings
from langchain.storage import LocalFileStore
from langchain_community.embeddings.huggingface import HuggingFaceEmbeddings
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser
from langchain_core.runnables import RunnablePassthrough
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_community.vectorstores.faiss import FAISS
from langserve import RemoteRunnable
from langchain_openai import ChatOpenAI
from langchain_core.callbacks.streaming_stdout import StreamingStdOutCallbackHandler
from langchain.schema import Document

app = Flask(__name__)

# 요청을 관리하는 큐 생성
request_queue = queue.Queue()

# 동시 실행할 수 있는 최대 스레드 수 제한
max_workers = 2
semaphore = threading.Semaphore(max_workers)

USE_BGE_EMBEDDING = False

if not USE_BGE_EMBEDDING:
    os.environ["OPENAI_API_KEY"] = "YOUT_KEY"

LANGSERVE_ENDPOINT = "http://localhost:1234/v1"
OLLAMA = ChatOpenAI(
    base_url=LANGSERVE_ENDPOINT,
    api_key="lm-studio",
    model="mradermacher/Llama-3.1-Korean-8B-Instruct-GGUF",
    streaming=True,
    callbacks=[StreamingStdOutCallbackHandler()],
)

RAG_PROMPT_TEMPLATE = """당신은 질문에 친절하게 답변하는 AI 입니다. 검색된 다음 문맥을 사용하여 질문에 답하세요. 답을 모른다면 모른다고 답변하세요.
Question: {question} 
Context: {context} 
Answer:"""

def process_request(data):
    with semaphore:
        question = data['question']
        member_id = data['member_id']
        user_data = data['user_data']

        print(question)

        context = format_user_data(member_id, user_data)
        retriever = embed_user_data(context)

        prompt = ChatPromptTemplate.from_template(RAG_PROMPT_TEMPLATE)

        rag_chain = (
            {
                "context": retriever | format_docs,
                "question": RunnablePassthrough(),
            }
            | prompt
            | OLLAMA
            | StrOutputParser()
        )

        answer = rag_chain.stream(question) 
        chunks = []
        for chunk in answer:
            chunks.append(chunk)
        
        ans = "".join(chunks)

        data['result'] = {
            "memberId": member_id,
            "ragData": ans
        }

        request_queue.task_done()

@app.route('/rag', methods=['POST'])
def rag():
    data = request.json
    question = data['question']
    member_id = data['memberId']
    user_data = data['chatData']

    request_data = {
        "question": question,
        "member_id": member_id,
        "user_data": user_data,
    }

    request_queue.put(request_data)

    worker_thread = threading.Thread(target=process_request, args=(request_data,))
    worker_thread.start()

    worker_thread.join()

    return jsonify(request_data['result'])

def format_user_data(member_id, chat_entries):
    formatted_data = []
    for entry in chat_entries:
        day = entry['day']
        contents = " ".join(entry['contents'])
        formatted_data.append(f"날짜: {day} - 내용: {contents}")
    return "\n\n".join(formatted_data)

def embed_user_data(text_data):
    if not isinstance(text_data, str):
        raise ValueError("텍스트 데이터는 문자열이어야 합니다.")
    
    print("Received text data:", text_data)

    cache_dir = LocalFileStore(f"./.cache/embeddings/user_data")
    
    docs = [Document(page_content=text_data)]

    # for doc in docs:  # 디버깅용 출력
    #     print("Document content:", doc.page_content)

    model_name = "BAAI/bge-m3"
    model_kwargs = {"device": "cuda"}
    encode_kwargs = {"normalize_embeddings": True}
    
    embeddings = HuggingFaceEmbeddings(
        model_name=model_name,
        model_kwargs=model_kwargs,
        encode_kwargs=encode_kwargs,
    )

    cached_embeddings = CacheBackedEmbeddings.from_bytes_store(embeddings, cache_dir)
    
    vectorstore = FAISS.from_documents(docs, embedding=cached_embeddings)
    retriever = vectorstore.as_retriever()

    return retriever

def format_docs(docs):
    return "\n\n".join(doc.page_content for doc in docs if hasattr(doc, "page_content"))

def dir_preprocess():
    if not os.path.exists(".cache"):
        os.mkdir(".cache")
    if not os.path.exists(".cache/embeddings"):
        os.mkdir(".cache/embeddings")
    if not os.path.exists(".cache/files"):
        os.mkdir(".cache/files")

@app.route('/test', methods=['GET'])
def test():
    sample_data = {
        "question": "오늘은 뭐 할까?",
        "userData": {
            "memberId": 1,
            "chatData": [
                {
                    "day": "2024-08-10",
                    "contents": [
                        "마라톤 참가하기",
                        "30분 운동 하기"
                    ]
                },
                {
                    "day": "2024-08-09",
                    "contents": [
                        "감자튀기 먹어버리기!"
                    ]
                },
                {
                    "day": "2024-08-08",
                    "contents": [
                        "바닷가 가기"
                    ]
                }
            ]
        }
    }
    return jsonify(sample_data)

if __name__ == '__main__':
    dir_preprocess()
    app.run(host='0.0.0.0', port=5000)