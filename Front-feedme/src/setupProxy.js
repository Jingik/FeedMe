const { createProxyMiddleware } = require("http-proxy-middleware");

module.exports = (app) => {
    app.use(
        "/ws",
        createProxyMiddleware({
            target: "http://localhost:8080/api/",
            ws: false,
            changeOrigin: true,
        })
    );
};