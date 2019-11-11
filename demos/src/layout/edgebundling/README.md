# Edge Bundling
  

 Choose between several layout algorithms and sample graphs and modify the strength of the bundling to see the effect on the edge curves.   

 The edge curves are drawn using piecewise cubic bezier curves with gradient colors from dark-blue (that starts from the source node) to light-blue (that leads to the target node of the edge). For the approximation of the edge curves, a [yfiles.layout.CurveFittingLayoutStage](https://docs.yworks.com/yfilesjava/doc/api/#/api/com.yworks.yfiles.layout.CurveFittingLayoutStage) is applied.   