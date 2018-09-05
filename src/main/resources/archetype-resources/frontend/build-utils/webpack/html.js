import HtmlWEbpackPlugin from "html-webpack-plugin";

export default () => ({
  plugins: [
    new HtmlWEbpackPlugin({
      template: "./src/index.hbs",
      filename: "index.html"
    })
  ]
});
