function changeDigitalPin(pinNumber) {
    var val = $('select[name=digitalPin' + pinNumber + ']').val();

    var success = $('input[name=success]').val();
    var fail = $('input[name=fail]').val();

    $.ajax({
        type: 'GET',
        url: '/digital/'+pinNumber + '/' + val,
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

function changeAnalogPin(pinNumber) {
    var val = $('input[name=analogPin' + pinNumber + ']').val();

    var success = $('input[name=success]').val();
    var fail = $('input[name=fail]').val();

    $.ajax({
        type: 'GET',
        url: '/analog/'+pinNumber + '/' + val,
        success: function (msg) {
            //$('option[name='+pinNumber+'_'+val+']').removeAttr('selected');
            //$('option[name='+pinNumber+'_'+val+']').attr('selected', true);
            $('div[class=message]').html('');
            $('div[class=message]').append('<div class="alert alert-success alert-dismissible" role="alert"><button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>'+success+'</div>')
        },
        error: function (msg) {
            console.log(msg);
            $('div[class=message]').html('');
            $('div[class=message]').append('<div class="alert alert-danger alert-dismissible" role="alert"><button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>'+msg.responseText+'</div>')
        }
    });
}

function updateValues() {
    $.ajax({
        type: 'GET',
        url: '/list',
        success: function (msg) {
           // console.log($.evalJSON(msg));
            //console.log(msg);
            var on = $('input[name=pinStateOn]').val();
            var off = $('input[name=pinStateOff]').val();

            msg.digital.forEach(function (element, index, array) {
                $('span[name=digital'+element.pinNumber+']').html(element.pinState);
                if (element.pinState == on) {
                    $('span[name=digital'+element.pinNumber+']').attr('class','label label-success');
                } else {
                    $('span[name=digital'+element.pinNumber+']').attr('class','label label-danger');
                }

            });

            msg.analog.forEach(function (element, index, array) {
                $('span[name=analog'+element.pinNumber+']').html(element.pinValue);

            })
         //   console.log(json);
        },
        error: function (msg) {

        }
    });
}

$(document).ready(function() {
    setInterval(updateValues, 500);
});
