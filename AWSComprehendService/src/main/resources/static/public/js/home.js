
function searchTerm() {
  showSpinner();

  var searchTerm = document.getElementById('term-input').value;

  var searchTermDisplay = document.getElementById("search-term");
  searchTermDisplay.innerHTML = searchTerm;
  populateTwitterData(searchTerm);
}

function showTwitterResults() {
  document.getElementById('key-phrases').style.display = "flex";
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
