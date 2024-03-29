import path from "path";
import { pathConfig } from "../../../path.config.js";

export const handlebars = {
  module: {
    rules: [
      {
        test: /\.hbs$/,
        use: [
          {
            loader: "handlebars-loader",
            options: {
              partialDirs: [path.resolve(pathConfig.paths.src)]
            }
          }
        ]
      }
    ]
  }
};
