import os
os.environ["CUDA_DEVICE_ORDER"] = "PCI_BUS_ID"
os.environ["CUDA_VISIBLE_DEVICES"] = "1"

from anything_control_pipeline import AnythingControlPipeline

if __name__ == "__main__":
    # 요청 대기
    while(True):
        model = AnythingControlPipeline()
        images = model.pipe()

    

        # for i, x in enumerate(images):
        #     x.save(f"tt{i}.gif", save_all=True)
        # 반환