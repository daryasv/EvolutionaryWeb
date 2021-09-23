var SETTINGS = buildUrlWithContextPath("/pages/evolutionrun/settings");

function getId(){
  const urlSearchParams = new URLSearchParams(window.location.search);
  return urlSearchParams.get("id");
}

var id = getId();

$(function() {
  $('#ev_id').text(id);

  $.ajax({
    url: SETTINGS + "?evoId="+id,
    success: function(evolutionData) {
        $('#settings-text').val(evolutionData.settings);
    }
  });
});

function back(){
  location.href = "../chatroom/chatroom.html";
}