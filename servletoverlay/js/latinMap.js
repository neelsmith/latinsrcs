var linkBase = "textsForSite.groovy?site="

var width = 800,
    height = 600;

var radius = 3;

var proj = d3.geo.mercator()
    .center([20,42])
    .scale([800])  ;


var path = d3.geo.path()
    .projection(proj);

var zoom = d3.behavior.zoom()
    .translate(proj.translate())
    .scale(proj.scale())
    .scaleExtent([height*.33, 4 * height])
    .on("zoom", zoom);


var svg = d3.select("#map").append("svg")
    .attr("width", width)
    .attr("height", height)
    .call(zoom);

function zoom() {
    proj.translate(d3.event.translate).scale(d3.event.scale);
    svg.selectAll("path").attr("d", path);
    circles
  	.attr("cx", function(d){return proj([d.lon, d.lat])[0];})
	.attr("cy", function(d){return proj([d.lon, d.lat])[1];});
}

var borders = svg.append("g");

var inscriptions = svg.append("g");

var tooltip = d3.select("body").append("div")
    .attr("class", "tooltip")
    .style("opacity", 1e-6)
    .style("background", "rgba(250,250,250,.7)");

queue()
    .defer(d3.json, "data/world50.topojson")
    .defer(d3.csv, "data/inscrlocs.csv")
    .await(ready);

var inscrs;
function ready(error, topology, csv){
    console.log(topology.objects);
    borders.selectAll("path")
	.data(topojson.object(topology, topology.objects.world50m).geometries)
	.enter()
	.append("path")
	.attr("d", path)
	.attr("class", "border")
	
    rawCsv = csv;
    inscrs = [];
    rawCsv.forEach(function(d){
	d.id = d.urn;
	inscrs.push(d);
    });
    inscrs.sort(function(a, b){return a.id - b.id;})

    circles = inscriptions.selectAll("circle")
	.data(inscrs).enter()
	.append("svg:a")
	.attr("xlink:href", function(d) { return linkBase + d.urn; })
	.attr("xlink:show", "new")
	.append("circle")
	.attr("cx", function(d){
	    return proj([d.lon, d.lat])[0];})
	.attr("cy", function(d){return proj([d.lon, d.lat])[1];})
	.attr("r", function(d){
	    return radius;
	})
	.attr("id", function(d){return "id" + d.id;})
	.style("fill", function(d){
	    return d3.rgb(255,0,0);
	})
	.on("mouseover", function(d){
	    d3.select(this)
		.attr("stroke", "black")
		.attr("stroke-width", 1)
		.attr("fill-opacity", 1);

	    tooltip
		.style("left", (d3.event.pageX + 5) + "px")
		.style("top", (d3.event.pageY - 5) + "px")
		.transition().duration(300)
		.style("opacity", 1)
		.style("display", "block")

	    updateDetails(d);
	})
	.on("mouseout", function(d){
	    d3.select(this)
		.attr("stroke", "")
		.attr("fill-opacity", function(d){return 1;})

	    tooltip.transition().duration(700).style("opacity", 0);
	});

    
    function render(method){
	d3.select(this).call(method);
    }

    lastFilterArray = [];
    inscrs.forEach(function(d, i){
	lastFilterArray[i] = 1;
    });

}


// pairings of variable names with printable labels:
var printDetails = [
    {'var': 'name', 'print': 'Name'}
];


function updateDetails(site){
    tooltip.selectAll("div").remove();
    tooltip.selectAll("div").data(printDetails).enter()
	.append("div")
	.append('span')
	.text(function(d){return d.print + ": ";})				
	.attr("class", "boldDetail")
	.insert('span')
	.text(function(d){return site[d.var];})
	.attr("class", "normalDetail");
}
