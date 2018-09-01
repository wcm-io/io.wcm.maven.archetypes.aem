import CopyWebpackPlugin from "copy-webpack-plugin";

export default () => ({
  plugins: [
    new CopyWebpackPlugin([
      {
        from: "./public/**/*.*",
        to: "./" // => dist
      }
    ])
  ]
});
