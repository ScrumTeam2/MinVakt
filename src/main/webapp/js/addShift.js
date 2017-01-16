/**
 * Created by olekristianaune on 16.01.2017.
 */


function submitForm(form) {
    var data = $(form).serializeArray()
        .reduce(function(a, x) { a[x.name] = x.value; return a; }, {});

    console.log(data);

    $.ajax({
        url: "/rest/shift",
        type: "POST",
        data: data
    })
    .done(function() {
        console.log( "success" );
    })
    .fail(function() {
        console.log( "error" );
    })
    .always(function() {
        console.log( "complete" );
    });

    return false;
}