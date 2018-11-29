
function searchTerm() {
  var searchTerm = document.getElementById('term-input').value;

  //display currently searched term
  document.getElementById("search-term").innerHTML = searchTerm;

  //search twitter
  populateTwitterData(searchTerm);
}
