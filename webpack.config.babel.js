const webpack = require("webpack");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const CopyWebpackPlugin = require("copy-webpack-plugin");
const CleanWebpackPlugin = require("clean-webpack-plugin");
const CleanCSSPlugin = require('less-plugin-clean-css');
const { name, version } = require("./package.json");

const srcDir = "./src/main/javascript";
const resourcesDir = "./src/main/resources/public/static";
const targetDir = "./target/javascript";
const targetResourcesDir = "static";
const configuration = (() => {
    const i = process.argv.indexOf("--c");
    const c = i !== -1 ? process.argv[i + 1] : null;
    if (!c) throw "Configuration json file not set [use node env argument: --c $c].";
    return require(c);
})();

const buildMode = process.argv.indexOf("-d") !== -1 ? "development" : "production";
const buildName = `${name}-${version}`;
const buildFile = "./build.js";
const buildPath = `${targetDir}/${buildName}-${buildMode}`;
const buildGeneratedPath = "./generated";
const buildBundleFile = "bundle.js";
console.info(`#------------------------Webapp build [${buildFile}]-----------------------#`);
console.info(`Build name: ${buildName}.`);
console.info(`Build mode: ${buildMode}.`);
console.info(`Build path: ${buildPath}.`);
console.info(`Build bundle file: ${buildBundleFile}.`);
console.info(`Build configuration: ${JSON.stringify(configuration)}.`);

module.exports = {
    entry: `${buildFile}`,
    output: { path: `${buildPath}`, filename: `${buildBundleFile}` },
    module: {
        preLoaders: [
            { test: /\.json$/, exclude: /node_modules/, loader: "json"},
        ],
        loaders: [
            { test: /\.js$/, exclude: /node_modules/, loader: "babel-loader" },
            { test: /\.css$/, loader: "style!css" },
            { test: /\.less$/, exclude: /node_modules/, loader: "style!css!less?strictMath&noIeCompat",  options: { LessPlugins: new CleanCSSPlugin({ advanced: true }) }},
            { test: /\.png$/, exclude: /node_modules/, loader: "url-loader?limit=100000" },
            { test: /\.jpg$/, exclude: /node_modules/, loader: "file-loader" },
            { test: /\.woff(2)?(\?v=[0-9]\.[0-9]\.[0-9])?$/, loader: `url-loader?limit=10000&minetype=application/font-woff&name=${buildGeneratedPath}/fonts/[hash].[ext]` },
            { test: /\.(ttf|eot|svg)(\?v=[0-9]\.[0-9]\.[0-9])?$/, loader: `file-loader?name=${buildGeneratedPath}/fonts/[hash].[ext]` }
        ]
    },
    plugins: [
        new webpack.DefinePlugin({
            "process.env": {
                "NODE_ENV": JSON.stringify(buildMode)
            },
            "name": JSON.stringify(name),
            "version": JSON.stringify(version),
            "configuration": JSON.stringify(configuration)
        }),
        new HtmlWebpackPlugin({
            template: `${srcDir}/index.ejs`,
            title: 'Webapp',
            inject: true,
            hash: true,
            cache: true
        }),
        new CopyWebpackPlugin([{ from: `${resourcesDir}`, to: `${targetResourcesDir}`}]),
        new CleanWebpackPlugin([buildPath], {
            exclude: ["index.html", "bundle.js"]
        })
    ],
    node: {
        fs: "empty"
    }
};

const optimize = buildMode == "production";
console.info(`Build optimize: ${optimize}.`);
if (optimize) {
    module.exports.plugins.push(new webpack.optimize.UglifyJsPlugin({
        compress:{
            warnings: true
        }
    }));
}