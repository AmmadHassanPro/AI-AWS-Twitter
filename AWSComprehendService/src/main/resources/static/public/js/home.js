
function getSearchTerm() {
  return document.getElementById('term-input').value;
}

function searchTerm() {
  showSpinner();

  var searchTerm = getSearchTerm();
  populateTwitterData(searchTerm);
}

function showTwitterResults() {
  document.getElementById('key-phrases').style.display = "flex";
  var searchTerm = getSearchTerm();
  var searchTermDisplay = document.getElementById('search-term-show');
  searchTermDisplay.innerHTML = searchTerm;
}

function showSpinner() {
  var spinnerModal = document.getElementById('loading-modal');
  // spinnerModal.style.display = "block";
  spinnerModal.classList.remove('hide');
  spinnerModal.classList.add('show');
}

function hideSpinner() {
  var spinnerModal = document.getElementById('loading-modal');
  // spinnerModal.style.display = "none";
  spinnerModal.classList.remove('show');
  spinnerModal.classList.add('hide');
}
