// Charts Controller
app.controller("chartsCtrl", function($rootScope, $scope, $http) {
	
	$scope.CHART_ENUM_STD_GROWTH = 1;
	$scope.CHART_ENUM_STD_GROWTH_NORM = 2;

	$scope.chartToNameMap = {};
	$scope.chartToNameMap[$scope.CHART_ENUM_STD_GROWTH] = "stdgrowth";
	$scope.chartToNameMap[$scope.CHART_ENUM_STD_GROWTH_NORM] = "stdgrowthnorm";
	
	$scope.chartToGraphIdMap = {};
	$scope.chartToGraphIdMap[$scope.CHART_ENUM_STD_GROWTH] = "graphIdStdGrowth";
	$scope.chartToGraphIdMap[$scope.CHART_ENUM_STD_GROWTH_NORM] = "graphIdStdGrowthNorm";
	
	$scope.chartToGraphLegendIdMap = {};
	$scope.chartToGraphLegendIdMap[$scope.CHART_ENUM_STD_GROWTH] = "graphIdStdGrowthLegend";
	$scope.chartToGraphLegendIdMap[$scope.CHART_ENUM_STD_GROWTH_NORM] = "graphIdStdGrowthNormLegend";
	
	$scope.DATEAXIS_NAME = "Date";
	$scope.CHARTSERIES_PRINCIPAL_ID = 1;
	$scope.CHARTSERIES_BENCHMARK_ID = 2;
	$scope.CHARTSERIES_USERPORTFOLIO_ID = 3;
	
	$scope.chartSeriesIdToNameMap = {};
	$scope.chartSeriesIdToNameMap[$scope.CHARTSERIES_PRINCIPAL_ID] = "Cash";
	$scope.chartSeriesIdToNameMap[$scope.CHARTSERIES_BENCHMARK_ID] = "Benchmark";
	$scope.chartSeriesIdToNameMap[$scope.CHARTSERIES_USERPORTFOLIO_ID] = $rootScope.authSession.username;

	$scope.graphMsgStdGrowth = null;
	$scope.graphMsgStdGrowthNorm = null;
	
	
	$scope.loadGraph = function(chartEnum, graphData) {
		var gStdGrowth = new Dygraph(
				document.getElementById($scope.chartToGraphIdMap[chartEnum]),
				graphData,
				{
					includeZero: true,
					colors: ["rgb(200,200,200)", "rgb(100,100,100)", "rgb(0,0,0)"],
					axisLabelWidth: 80,
					axisLabelFontSize: 12,
					digitsAfterDecimal: 0,
					labelsSeparateLines: false,
					labelsDiv: $scope.chartToGraphLegendIdMap[chartEnum],
					hideOverlayOnMouseOut: false
				}
		);
	};
	
	//
	// Utility function to parse graph data and load graph
	// Returns true if data was loaded successfully; false otherwise
	//
	$scope.parseAndLoadGraphData = function(chartEnum, series) {
		var result = false;
		
		var i;
		var j;
		var validData = true;
		for (i = 0; i < series.length; i++) {
			if (series[i].points == null || series[i].points.length < 1) {
				validData = false;
				break;
			}
		}
		
		if (validData) {
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
			$scope.loadGraph(chartEnum, gData);
			result = true;
		}
		
		return result;
	};
	
	//
	// Generate Graphs
	//
	$scope.generateGraphs = function() {
		
		// STD GROWTH GRAPH
		$scope.graphMsgStdGrowth = "Building Portfolio Benchmark Chart...";
		$http({
			  method: "GET",
			  url: "api/v1/chart/" + $scope.chartToNameMap[$scope.CHART_ENUM_STD_GROWTH]
			}).then(
				function successCallback(response) {
					// non-empty series data
					if (response.data != null && response.data.seriesList != null && 
									response.data.seriesList.length > 0) {
						var series = response.data.seriesList;
						
						var graphSuccess = $scope.parseAndLoadGraphData($scope.CHART_ENUM_STD_GROWTH, series);
						if (graphSuccess) {	
							$scope.graphMsgStdGrowth = "Portfolio vs Benchmark";
						}
						else {
							$scope.graphMsgStdGrowth = "Portfolio Benchmark Chart Not Available";
						}
					}
					else {
						$scope.graphMsgStdGrowth = "Portfolio Benchmark Chart Not Available";
					}
				},
				function errorCallback(response) {
					$scope.graphMsgStdGrowth = "Portfolio Benchmark Chart Not Available";
				}
		);
		
		// STD GROWTH NORM GRAPH
		$scope.graphMsgStdGrowthNorm = "Building $10,000 Growth Chart...";
		$http({
			  method: "GET",
			  url: "api/v1/chart/" + $scope.chartToNameMap[$scope.CHART_ENUM_STD_GROWTH_NORM]
			}).then(
				function successCallback(response) {
					// non-empty series data
					if (response.data != null && response.data.seriesList != null && 
									response.data.seriesList.length > 0) {
						var series = response.data.seriesList;
						
						var graphSuccess = $scope.parseAndLoadGraphData($scope.CHART_ENUM_STD_GROWTH_NORM, series);
						if (graphSuccess) {	
							$scope.graphMsgStdGrowthNorm = "$10,000 Growth";
						}
						else {
							$scope.graphMsgStdGrowthNorm = "$10,000 Growth Chart Not Available";
						}
					}
					else {
						$scope.graphMsgStdGrowthNorm = "$10,000 Growth Chart Not Available";
					}
				},
				function errorCallback(response) {
					$scope.graphMsgStdGrowthNorm = "$10,000 Growth Chart Not Available";
				}
		);
	};
	
	$scope.generateGraphs();
	
});
