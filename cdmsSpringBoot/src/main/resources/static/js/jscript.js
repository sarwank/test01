const urls = "http://local.cdmswebapp.live:31000/";
// const urls = "https://documentmanager.cf/";
const getCellValue = (tr, idx) => tr.children[idx].innerText || tr.children[idx].textContent;

const comparer = (idx, asc) => (a, b) => ((v1, v2) =>
        v1 !== '' && v2 !== '' && !isNaN(v1) && !isNaN(v2) ? v1 - v2 : v1.toString().localeCompare(v2)
)(getCellValue(asc ? a : b, idx), getCellValue(asc ? b : a, idx));

document.querySelectorAll('th').forEach(th => th.addEventListener('click', (() => {
    const table = th.closest('table');
    const tbody = table.querySelector('tbody');
    Array.from(tbody.querySelectorAll('tr'))
        .sort(comparer(Array.from(th.parentNode.children).indexOf(th), this.asc = !this.asc))
        .forEach(tr => tbody.appendChild(tr));
})));

$(document).ready(function () {
    $("#myInput").on("keyup", function () {
        var value = $(this).val().toLowerCase();
        $("#table tbody tr").filter(function () {
            $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1)
        });
    });
});

$(function () {
    $("[data-toggle='tooltip']").tooltip();
})
var settings = {
    "async": true,
    "crossDomain": true,
    "url": urls + "s3/bucket/files/",
    "method": "GET",
    "headers": {
        "cache-control": "no-cache"
    }
}
var selectedFiles = [];
$.ajax(settings).done(function (data) {
    console.log(data);
    var table = $("#table tbody");
    var tdCnt = 0;
    $.each(data, function (idx, elem) {
        table.append("<tr><td><input type=\"checkbox\" class=\"dt-checkboxes\" id=\"id" + tdCnt + "\"/></td><td class=\"type\"><i class=\"fa fa-file text-primary\"></i></td><td class=\"name_truncate\"><a href=\"" + urls + "s3/file/download/" + elem.fileName + "\">" + elem.fileName + "</a></td><td class=\"date\">" + elem.lastModified + "</td><td class=\"size\">" + elem.fileSize + " KB</td></tr>");
        tdCnt++;
    });
});
$(document).ready(function () {
    $('#table').on('click', ':checkbox', function () {
        var fileName = $(this).closest('tr').find('.name_truncate').text();
        const index = selectedFiles.indexOf(fileName);
        if (index > -1) {
            selectedFiles.splice(index, 1); // 2nd parameter means remove one item only
            alert("Removed File : " + fileName);
        } else {
            selectedFiles.push(fileName);
            alert("Added File : " + fileName);
        }
        console.log("After operation  :", selectedFiles);
    });
});
$(document).ready(function () {
    $('#file').change(function () {
        var formData = new FormData();
        formData.append("file", this.files[0]);
        var settings = {
            type: "POST",
            url: urls + "s3/file/upload/",
            data: formData,// now data come in this function
            contentType: false,
            processData: false,
            mimeType: "multipart/form-data",
            crossDomain: true
        }
        $.ajax(settings).done(function (response) {
            if (response === "Exception") {
                alert("Something Went Wrong");
            } else {
                alert("File " + formData.get("file").name + " Uploaded Successfully");
            }
            console.log(response);
            location.reload();
        });
        this.value = null;
    });
});
$(document).ready(function () {
    $('#idDelete').click(function () {
        for (const fil of selectedFiles) {
            var formData = new FormData();
            var settings = {
                type: "DELETE",
                url: urls + "s3/file/delete/" + fil,
                data: formData,// now data come in this function
                contentType: false,
                processData: false,
                mimeType: "multipart/form-data",
                crossDomain: true
            }
            $.ajax(settings).done(function (response) {
                console.log(response);
            });
        }
        if (selectedFiles.length === 0) {
            alert("No Files Selected");
        } else {
            alert("Selected file Deleted Successfully");
            location.reload();
        }
    });
});
$(document).ready(function () {
    $('#idRefresh').click(function () {
        location.reload();
    });
});
$(document).ready(function () {
    if ($('#check').is(':checked')) {
        $('span').html("Notification will send");
    }
    $('#check').on('change', function () {
        var checked = this.checked
        $('span').html(checked.toString() === "true" ? "Notification will send " : "No Notification will send");
        var form = new FormData();
        form.append("status", checked);

        var settings = {
            "async": true,
            "crossDomain": true,
            "url": urls + "notificationStatusUpdate",
            "method": "POST",
            "headers": {
                "cache-control": "no-cache",
            },
            "processData": false,
            "contentType": false,
            "mimeType": "multipart/form-data",
            "data": form
        }
        $.ajax(settings).done(function (response) {
            console.log(response);
        });
    });
});
