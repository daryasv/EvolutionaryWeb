var SETTINGS = buildUrlWithContextPath("settings");
var RUN_URL = buildUrlWithContextPath("run_evolution");
var SHOW_SOLUTION_URL = buildUrlWithContextPath("solution");


function getId(){
  const urlSearchParams = new URLSearchParams(window.location.search);
  return urlSearchParams.get("id");
}

var id = getId();
var running = false;
var paused = false;
var mutationsCount = 0;
var enableViewOptions = false;

function back(){
  location.href = "../chatroom/chatroom.html";
}

function printMutations(mutations) {
  var mutationsHtml ="";
  for(var i =0; i<mutations.length ;i++) {
    var mutation = mutations[i];

    mutationsHtml += ("<div id='mutationRow"+(i)+"'  class=\"row form-row\">\n" +
        "            <div class=\"form-group col-md-3\">\n" +
        "                <label for=\"mutationType"+i+"\">Mutation type</label>\n" +
        "                <select id='mutationType"+(i)+"'  class=\"form-control\">\n" +
        "                    <option value=''>Choose...</option>\n" +
        "                    <option value=\"Flipping\">Flipping</option>\n" +
        "                    <option value=\"Sizer\">Sizer</option>\n" +
        "                </select>\n" +
        "            </div>\n" +
        "            <div class=\"form-group col-md-3\">\n" +
        "                <label for=\"probability"+i+"\">Probability (1-100)</label>\n" +
        "                <input type=\"number\" value="+(mutation.probability)+" class=\"form-control\" id=\"probability"+i+"\" placeholder=\"Probability\">\n" +
        "            </div>\n" +
        "            <div class=\"form-group col-md-3\">\n" +
        "                <label for=\"tupples"+i+"\">Max Tupples</label>\n" +
        "                <input type=\"number\" value="+(mutation.maxTupples)+" class=\"form-control\" id=\"tupples"+i+"\" placeholder=\"Tupples\">\n" +
        "            </div>\n" +
        "            <div class=\"form-group col-md-1\">\n" +
        "                <label for=\"component"+i+"\">Component</label>\n" +
        "                <select id=\"component"+i+"\" class=\"form-control\" value="+(mutation.component)+">\n" +
        "                    <option "+(mutation.component === "D" ? "selected" : "")+" value=\"D\">D</option>\n" +
        "                    <option "+(mutation.component === "H"? "selected" : "")+" value=\"H\">H</option>\n" +
        "                    <option "+(mutation.component === "T"? "selected" : "")+" value=\"T\">T</option>\n" +
        "                    <option "+(mutation.component === "C"? "selected" : "")+" value=\"C\">C</option>\n" +
        "                    <option "+(mutation.component === "S"? "selected" : "")+" value=\"S\">S</option>\n" +
        "                </select>\n" +
        "            </div>\n" +
        "            <div class=\"form-group col-md-2\">\n" +
        "               <button id='mutationBtn"+(i)+"' onclick='removeMutation("+(i)+")' class=\"btn btn-danger\">Remove</button>" +
        "            </div>\n" +
        "        </div>");
  }
  $('#mutationsRows').html(mutationsHtml);

}

function loadPageData(evolutionData,all){
  if(all) {
    $('#settings-text').val(evolutionData.settings);
    if (evolutionData.evConfig) {
      $('#inputPopSize').val(evolutionData.evConfig.populationSize);
      $('#selectionInput').val(evolutionData.evConfig.selectionType).change();
      $('#selectionPercentInput').val(evolutionData.evConfig.selectionPercentage);
      $('#elitismInput').val(evolutionData.evConfig.elitismSize);
      $('#crossoverType').val(evolutionData.evConfig.crossoverType).change();
      $('#cuttingPointsInput').val(evolutionData.evConfig.cuttingPoints);
      $('#orientationInput').val(evolutionData.evConfig.orientationType).change();
      if(evolutionData.evConfig.mutations) {
        mutationsCount =  evolutionData.evConfig.mutations.length;
        printMutations(evolutionData.evConfig.mutations);
      }else{
        mutationsCount = 0;
      }
    }
    $('#currentGen').text(evolutionData.currentGeneration || 0);
    $('#bestFitness').text((evolutionData.bestFitness || 0).toFixed(2)+"%");
  }
  const percentage = evolutionData.percentage || 0;

  $('#progress-bar').css('width', (percentage) + '%');
  $('#progress-text').text((percentage.toFixed(2)) + '%');

  if(enableViewOptions !== evolutionData.viewingOptions) {
    enableViewOptions = !enableViewOptions;
    if(enableViewOptions) {
      $('#viewingOptions').attr("class","row");
      if (evolutionData.solutionFitness) {
        $('#solutionTable').html(evolutionData.solutionFitness);
      }
    }else{
      $('#viewingOptions').attr("class","hidden");
      $('#solutionTable').html("");
    }
  }

  var changed = false;
  if(evolutionData.paused !== paused){
    paused = !paused;
    changed = true;
  }
  if(evolutionData.running !== running){
    running = !running;
    changed = true;
  }
  if(changed){
    if(running){
      $('#run_evolution').attr("disabled",false).attr("class","btn btn-secondary").text("Pause");
      $('#stop_evolution').attr("class","btn btn-danger").attr("disabled",false);
    }else {
      $('#run_evolution').attr("disabled",false).attr("class","btn btn-primary").text(paused ? "Resume" : "Start");
      $('#stop_evolution').attr("class",paused? "btn btn-danger": "btn btn-secondary").attr("disabled",!paused);
    }
  }
  if(running){
    $('#currentGen').text(evolutionData.currentGeneration || 0);
    $('#bestFitness').text((evolutionData.bestFitness || 0).toFixed(2) + "%");
  }
}

function getPageData(all){
  $.ajax({
    url: SETTINGS + "?evoId="+id,
    success: function(evolutionData) {
      loadPageData(evolutionData,all);
    }

  });
}

function initElements() {

  $('#ev_id').text(id);
  $('#crossoverType').on('change', function (e) {
    var val = $(e.target).val();
    $('#orientationInput').attr("disabled", val !== "AspectOriented");
  });
  $('#selectionInput').on('change', function (e) {
    var val = $(e.target).val();
    $('#selectionPercentInput').attr("disabled", val !== "Truncation");
  });

  $('#endConditionSelect').on('change', function (e) {
    var val = $(e.target).val();
    $('#limitInput').attr("disabled", !val || val === "Choose...");
    $('#limitLabel').text(val === "Time" ? "Limit (Seconds)" : "Limit");
  });

  $('#viewOptionSelect').on('change', function (e) {
    var val = $(e.target).val();
    if (val === "teacher" || val === "class") {
      $('#tableIdContainer').attr("class", "form-group col-md-4");
      $('#objectIdLabel').text(val === "teacher" ? "Teacher id" : "Class id");
    } else {
      $('#tableIdContainer').attr("class", "hidden");
    }
  });
  $('#addMutationBtn').on('click', (e) => addMutation(e));
}

function getRunConfig() {
  var mutations = [];
  for (var i =0;i<mutationsCount;i++){
    mutations.push({
      name: $('#mutationType'+i).val(),
      probability:$('#probability'+i).val(),
      maxTupples:$('#tupples'+i).val(),
      component:$('#component'+i).val(),
    })
  }
  return {
    "evoId": id,
    "evoPause": running,
    "evoStop": false,
    populationSize: $('#inputPopSize').val(),
    selectionType: $('#selectionInput').val(),
    selectionPercentage: $('#selectionPercentInput').val(),
    elitismSize: $('#elitismInput').val(),
    crossoverType: $('#crossoverType').val(),
    cuttingPoints: $('#cuttingPointsInput').val(),
    orientationType: $('#orientationInput').val(),
    endCondition: $('#endConditionSelect').val(),
    endConditionLimit: $('#limitInput').val(),
    generationsInterval: $('#intervalInput').val(),
    mutations:mutations
  };
}

$(function() { // onload...do
  initElements();

 getPageData(true);

  setInterval(()=>getPageData(false),1000);

  $("#run_evolution").on("click",function(e) {
    var url = RUN_URL;
    e.preventDefault();

    $.ajax({
      data: getRunConfig(),
      url: url,
      // timeout: 10000,
      method: "POST",
      error: function(error) {
        console.error("Failed to submit");
        alert(error.responseText || "Something went wrong. Check the details and try again")
        $('#run_evolution').attr("disabled",false);
      },
      success: function(r) {
        $('#run_evolution').attr("disabled",false).attr("class","btn btn-secondary").text("Pause");
        $('#stop_evolution').attr("class","btn btn-danger").attr("disabled",false);
        if(!running) {
          running = true;
          if(!paused) {
            $('#progress-bar').css('width', '0%');
            $('#progress-text').text('0%');
          }else{
            paused = false;
          }
        }
      }
    });

    $('#run_evolution').attr("disabled",true);
    $('#stop_evolution').attr("disabled",true);
    return false;
  });

  $("#stop_evolution").on("click",function(e) {
    var url = RUN_URL+"?evoId="+id+"&evoPause="+running+"&evoStop=true";
    e.preventDefault();

    $.ajax({
      data: "",
      url: url,
      // timeout: 10000,
      method: "POST",
      error: function() {
        alert("Failed to stop")
        $('#run_evolution').attr("disabled",false);
        $('#stop_evolution').attr("disabled",false);
      },
      success: function(r) {
        alert("Stopping");
        $('#run_evolution').attr("disabled",false);
        $('#stop_evolution').attr("disabled",false);
      }
    });

    $('#run_evolution').attr("disabled",true);
    $('#stop_evolution').attr("disabled",true);
    return false;
  });




  $("#show_solution").on("click",function(e) {
    var url = SHOW_SOLUTION_URL;
    e.preventDefault();

    var type =  $('#viewOptionSelect').val();
    var objectId=0;
    if(type === "teacher" || type === "class"){
      objectId = $('#tableObjectId').val();
    }
    $.ajax({
      data: {
        evoId: id,
        type: type,
        objectId: objectId
      },
      url: url,
      // timeout: 10000,
      method: "POST",
      error: function(error) {
        console.error("Failed to submit");
        alert(error.responseText || "Something went wrong. Check the details and try again")
      },
      success: function(response) {
        if(type==="raw"){
             $('#solutionTable').html(response.RawSolution);
        }
        else{
             $('#solutionTable').html(response.solutionFitness);
        }
      }
    });


  });
});

function addMutation(){
  var i = mutationsCount;
  var mutationHtml = ("<div id='mutationRow"+(i)+"' class=\"row form-row\">\n" +
      "            <div class=\"form-group col-md-3\">\n" +
      "                <label for=\"mutationType"+i+"\">Mutation type</label>\n" +
      "                <select id='mutationType"+(i)+"'  class=\"form-control\">\n" +
      "                    <option value=''>Choose...</option>\n" +
      "                    <option value=\"Flipping\">Flipping</option>\n" +
      "                    <option value=\"Sizer\">Sizer</option>\n" +
      "                </select>\n" +
      "            </div>\n" +
      "            <div class=\"form-group col-md-3\">\n" +
      "                <label for=\"probability"+i+"\">Probability(1-100)</label>\n" +
      "                <input type=\"number\" value="+(0)+" class=\"form-control\" id=\"probability"+i+"\" placeholder=\"Probability\">\n" +
      "            </div>\n" +
      "            <div class=\"form-group col-md-3\">\n" +
      "                <label for=\"tupples"+i+"\">Max Tupples</label>\n" +
      "                <input type=\"number\" value="+(0)+" class=\"form-control\" id=\"tupples"+i+"\" placeholder=\"Tupples\">\n" +
      "            </div>\n" +
      "            <div class=\"form-group col-md-1\">\n" +
      "                <label for=\"component"+i+"\">Component</label>\n" +
      "                <select id=\"component"+i+"\" class=\"form-control\">\n" +
      "                    <option value=''>Choose...</option>\n" +
      "                    <option value=\"D\">D</option>\n" +
      "                    <option value=\"H\">H</option>\n" +
      "                    <option value=\"T\">T</option>\n" +
      "                    <option value=\"C\">C</option>\n" +
      "                    <option value=\"S\">S</option>\n" +
      "                </select>\n" +
      "            </div>\n" +
      "            <div class=\"form-group col-md-2\">\n" +
      "               <button id='mutationBtn"+(i)+"'  onclick='removeMutation("+(i)+")' class=\"btn btn-danger\">Remove</button>" +
      "            </div>\n" +
      "        </div>");
  mutationsCount ++;
  $('#mutationsRows').append(mutationHtml);

  return false;
}

function removeMutation(index) {
  $('#mutationRow' + index).remove();
  debugger
  try {
    for (var i = index + 1; i < mutationsCount; i++) {
      $('#mutationRow' + i).attr("id", "mutationRow" + (i - 1));
      $('#mutationType' + i).attr("id", 'mutationType' + (i - 1));
      $('#probability' + i).attr("id", 'probability' + (i - 1));
      $('#tupples' + i).attr("id", 'tupples' + (i - 1));
      $('#component' + i).attr("id", 'component' + (i - 1));
      $('#mutationBtn' + (i)).attr("id", "mutationBtn" + (i - 1)).attr("onclick", "removeMutation(" + (i - 1) + ")")
    }

    mutationsCount--;
  }catch (e){
    console.error(e);
  }
  return false;
}