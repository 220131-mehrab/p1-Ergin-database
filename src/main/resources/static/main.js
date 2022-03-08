var welcomeMsg = '  This is the welcomeMsg and there are two spaces in the beginning can you see that'

document.querySelector('h1').innerText = welcomeMsg;

fetch('/artists').then(resp => resp.json()).then(artists =>{
    document.querySelector('body').innerHTML = listArtists(artists)

});
    let listArtist = function(artist) {
    return '<p>' + artist.artistId + ": " + artist.name + '</p>';
}

function listArtists(json) {
    return`
        <div id="artistList">
            ${json.map(listArtist).join('\n')}
        </div>
    `
}

// function get(url){
//     return new Promise(function(resolve, reject){
//         var req = new XMLHttpRequest();
//         req.open('GET',url);
//         req.onload =  function() {
//             if (req.status == 200){
//                 resolve(req.response);
//             } else {
//                 reject(Error(req.statusText));
//             }
//     }
//         req.onerror = function() {
//             reject(Error("Network error"));
//         }
//         req.send();
//     }
//     )}

// get('/artists').then(function(response) {
//     console.log(JSON.parse(response))

// },function(err){

// })





