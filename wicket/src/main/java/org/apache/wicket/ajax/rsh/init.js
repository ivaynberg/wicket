window.dhtmlHistory.create();

var yourListener = function(newLocation, historyData) {
	Wicket.Ajax.historyGo(newLocation);
}

window.onload = function() {
	dhtmlHistory.initialize();
	dhtmlHistory.addListener(yourListener);
};
