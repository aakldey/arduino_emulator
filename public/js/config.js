function changeDigitalPin(pinNumber) {
    var val = $('select[name=digitalPin' + pinNumber + ']').val();

    var success = $('input[name=success]').val();
    var fail = $('input[name=fail]').val();

    $.ajax({
        type: 'GET',
        url: pinNumber + '/' + val,
        success: function (msg) {
            //$('option[name='+pinNumber+'_'+val+']').removeAttr('selected');
            //$('option[name='+pinNumber+'_'+val+']').attr('selected', true);
            $('div[class=message]').html('');
            $('div[class=message]').append('<div class="alert alert-success alert-dismissible" role="alert"><button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>'+success+'</div>')
        },
        error: function (msg) {
            $('div[class=message]').html('');
            $('div[class=message]').append('<div class="alert alert-danger alert-dismissible" role="alert"><button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>'+fail+'</div>')
        }
    });
}
