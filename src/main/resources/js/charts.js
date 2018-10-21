// Charts Controller
app.controller("chartsCtrl", function($rootScope, $scope, $http) {
	
	$scope.CHART_STD_GROWTH_ID = 1;

	$scope.chartIdToNameMap = {};
	$scope.chartIdToNameMap[$scope.CHART_STD_GROWTH_ID] = "stdgrowth";
	
	$scope.DATEAXIS_NAME = "Date";
	$scope.CHARTSERIES_PRINCIPAL_ID = 1;
	$scope.CHARTSERIES_TOTALUSMARKET_ID = 2;
	$scope.CHARTSERIES_USERPORTFOLIO_ID = 3;
	
	$scope.chartSeriesIdToNameMap = {};
	$scope.chartSeriesIdToNameMap[$scope.CHARTSERIES_PRINCIPAL_ID] = "Cash";
	$scope.chartSeriesIdToNameMap[$scope.CHARTSERIES_TOTALUSMARKET_ID] = "Tot Mkt";
	$scope.chartSeriesIdToNameMap[$scope.CHARTSERIES_USERPORTFOLIO_ID] = $rootScope.authSession.username;

	$scope.graphMsgStdGrowth = null;
	
	
	$scope.loadGraph = function(graphId, graphData) {
		var gStdGrowth = new Dygraph(
				document.getElementById(graphId),
				graphData,
				{
					includeZero: true,
					colors: ["rgb(180,180,180)", "rgb(120,120,120)", "rgb(0,0,0)"],
					axisLabelWidth: 80,
					axisLabelFontSize: 12,
					digitsAfterDecimal: 0,
					labelsSeparateLines: false,
					labelsDiv: "graphIdStdGrowthLegend",
					hideOverlayOnMouseOut: false
				}
		);
	};
	
	//
	// Generate Std Growth Graph
	//
	$scope.generateStdGrowthGraph = function() {
		$scope.graphMsgStdGrowth = "Building Portfolio Comparison Chart...";
		$http({
			  method: "GET",
			  url: "api/v1/chart/" + $scope.chartIdToNameMap[$scope.CHART_STD_GROWTH_ID]
			}).then(
				function successCallback(response) {
					// non-empty series data
					if (response.data != null && response.data.seriesList != null && 
									response.data.seriesList.length > 0) {
						var series = response.data.seriesList;
						
						var i;
						var j;
						var gData = "";
	
						// dygraphs format:
						// 1st line: Date,Col2,Col3\n
						// 2nd line: date,ser1pt1,ser2pt1\n
						// 3rd line: date,ser1pt2,ser2pt2\n
						// ...
						
						gData = gData + $scope.DATEAXIS_NAME;
						for (i = 0; i < series.length; i++) {
							var seriesId = series[i].type;
							gData = gData + "," + $scope.chartSeriesIdToNameMap[seriesId];
						}
						gData = gData + "\n";
						
						for (i = 0; i < series[0].points.length; i++) {
							gData = gData + series[0].points[i].date;
							for (j = 0; j < series.length; j++) {
								gData = gData + "," + series[j].points[i].value;
							}
							gData = gData + "\n";
						}
						
						// load graph object
						$scope.graphMsgStdGrowth = "My Portfolio vs Total Stock Market (5-year)";
						$scope.loadGraph("graphIdStdGrowth", gData);
					}
				},
				function errorCallback(response) {
					$scope.graphMsgStdGrowth = "Error loading Portfolio Comparison Chart";
				}
		);
	};
	
	$scope.generateStdGrowthGraph();
	
});
