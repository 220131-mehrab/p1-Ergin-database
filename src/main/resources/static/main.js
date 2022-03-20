var welcomeMsg = 'Music App';

document.querySelector('h1').innerText = welcomeMsg;

fetch('/artists').then(resp => resp.json()).then(artists =>{
    document.querySelector('#artists').innerHTML = listArtists(artists)});
    
function listArtists(json) {
        return `${json.map(listArtist).join('\n')}`;
    };

let listArtist = function(artist){
    return '<p>' + artist.artistId + ": " + artist.name + '</p>';
};

function postArtist(){
    let artist = {
    "artistId": document.getElementById("artistId").value,
    "name": document.getElementById("name").value
    }
    console.log(artist);
    fetch("/artists",{
        method: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type':'application/json'
        },
        body: JSON.stringify(artist)
    }).then((result)=>{
        if (result.status !=200) {
            throw new Error("Bad Server Response");
        }
        console.log(result.text());
    }).catch((error)=>{console.log(error);})
    fetch('/artists').then(resp => resp.json()).then(artists =>{
        document.querySelector('#artists').innerHTML = listArtists(artists)});

    fetch("https://data.kcmo.org/resource/frqd-pm5w")
    .then(response => response.json())
    .then(data => {
      data.forEach(item => console.log(item));   
    });
};