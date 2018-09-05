import path from "path";
import webpackMerge from "webpack-merge";

import handleSCSS from "./build-utils/webpack/scss";
import handleES6 from "./build-utils/webpack/es6";
import handleHtml from "./build-utils/webpack/html";
import handleClean from "./build-utils/webpack/clean";
import handleHandlebars from "./build-utils/webpack/handlebars";

export default (env, argv) => {
  return webpackMerge(
    {
      devtool: "source-map",
      output: {
        path: path.resolve(__dirname, "dist"),
        filename: "[name]"
      },
      devServer: {
        contentBase: "./public"
      }
    },
    handleClean(),
    handleES6(),
    handleSCSS(),
    handleHandlebars(),
    handleHtml()
  );
};
