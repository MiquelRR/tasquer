const SCRIPT_VERSION="??????";
const PROJECT_ID="??????????"


function onOpen() {
  var ui = SpreadsheetApp.getUi();
  ui.createMenu("APP functions")
    .addItem('Carregar tasques desde Firebase', 'getFirebaseTasks')
    .addToUi();
  //generarMapaDeImagenes(null);
  fetchAvatars();
  const appUsersList = {};
  const propiedades = PropertiesService.getDocumentProperties();
  propiedades.setProperty("appUsersList", JSON.stringify(appUsersList));
}


function downloadAllTasks() {
  const url=`https://--------------------------?sender=${SCRIPT_VERSION}`;
  try{
    Logger.log(url)
    const response = UrlFetchApp.fetch(url, {
      method: "GET",
      contentType: "application/json",
    });
    Logger.log(response)
    return JSON.parse(response.getContentText());
  } catch (error) {
    Logger.log("Error recuperant tasques")
  }
}

function getFirebaseTasks() {
  var sheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName("totes");
  if (!sheet) {
    Logger.log("Error: La hoja 'totes' no existe.");
    return;
  }

  var tasks = downloadAllTasks();
  var data = [];

  for (var key in tasks) {
    if (tasks.hasOwnProperty(key)) {
      var task = tasks[key];
      data.push([
        task.email || "",
        task.assignedUser || "",
        key,
        task.date || "",
        task.duration || "",
        task.description || "",
        task.done || false
      ]);
    }
  }

  sheet.getRange("A2:G1000").clearContent();
  if (data.length > 0) {
    sheet.getRange(2, 1, data.length, data[0].length).setValues(data);
  }
}
