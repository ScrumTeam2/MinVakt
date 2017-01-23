/**
 * Created by AnitaKristineAune on 17.01.2017.
 */

$(document).ready(function() {

    $(".container-header").hover(

        function () {
            $("#" + String(this.id)).css("background-color", "#f2f2f2");
        },

        function() {
            $("#" + String(this.id)).css("background-color", "#ffffff");
        }
    );

    $(".container-header").click(function(){
        $("#"+String(this.id)).css("background-color","#f2f2f2");

        // Vaktliste
        if(String(this.id)=="item1"){
            window.location="#";

         // Vaktbytte
        } else if(String(this.id)=="item2") {
            window.location = "#";

            // Ansatte
        } else if(String(this.id)=="item3") {
            window.location = "/html/user-e.html";

        // Fravær
        } else if(String(this.id)=="item4") {
            window.location = "#";

            // Timebank
        }else if(String(this.id)=="item5") {
            window.location = "#";

            // Tilgjengelighet
        }else if(String(this.id)=="item6") {
            window.location = "availability-service.html";

        } else{
            console.log("Something went wrong");
        }


    });

});