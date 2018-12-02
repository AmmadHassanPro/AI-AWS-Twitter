
function searchTerm() {
  var searchTerm = document.getElementById('term-input').value;

  //display currently searched term
  document.getElementById("search-term").innerHTML = searchTerm;
  document.getElementById('key-phrases').style.display = "flex";
  populateTwitterData(searchTerm);
}
