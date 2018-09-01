import path from "path";

export default () => ({
  module: {
    rules: [
      {
        test: /\.hbs$/,
        use: [
          {
            loader: "handlebars-loader",
            options: {
              partialDirs: [path.join("src")]
            }
          }
        ]
      }
    ]
  }
});
