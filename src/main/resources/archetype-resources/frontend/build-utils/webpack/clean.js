import CleanWebpackPlugin from 'clean-webpack-plugin';

const pathsToClean = [
  'dist'
]

const cleanOptions = {
  root: `${__dirname}/../..`,
  verbose: false
}

export default () => ({
  plugins: [
    new CleanWebpackPlugin(pathsToClean, cleanOptions)
  ]
});