import path from "path";

export default () => {
  return {
    entry: {
      "./static/js/index.js": "./src/index.js"
    },
    module: {
      rules: [
        {
          test: /\.js$/,
          exclude: "/node_modules/",
          loader: "babel-loader"
        }
      ]
    }
  };
};
