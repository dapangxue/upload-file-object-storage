$(document).ready(function() {
    $("#createBucket").click(function () {
        var bucketName = $("#bucketName").val();

        if (bucketName == "") {
            alert("bucket不能为空");
        } else {
            $.ajax({
                type: "POST",
                url: "/bucket/create",
                dataType:"json",
                data: {bucketName: bucketName}
            });
        }
    });
});