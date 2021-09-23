var SETTINGS = buildUrlWithContextPath("/pages/evolutionrun/settings");

function getId(){
  const urlSearchParams = new URLSearchParams(window.location.search);
  return urlSearchParams.get("id");
}

var id = getId();

$(function() {
  $.ajax({
    url: SETTINGS + "?evoId="+id,
    success: function(evolutionData) {
        $('#settings-text').val(evolutionData.settings);
    }
  });
});