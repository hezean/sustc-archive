const {defineConfig} = require('@vue/cli-service')
module.exports = defineConfig({
    transpileDependencies: true,
    devServer: {
        proxy: {
            '/api': {
                target: 'http://localhost:8085/',
                rewrite: (path) => path.replace(/^\/api/, '')
            }
        }
    },
})
