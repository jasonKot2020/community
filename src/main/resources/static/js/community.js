/*
导航栏跳转
 */
function setTopUrl(type,url) {
    // if($("#activeBut"+type).hasClass('active')){
    //     return;
    // }
    /*
    0首页
    1介绍
    2下载
     */
    window.location.href = url;
}

/*
tip box
 */
function showTip(mes,type,showTime) {
    var tipId = "#"+type+"Tip";

    $(tipId).text(mes);

    $(tipId).fadeIn(500);

    if(showTime == null || showTime < 2000){
        showTime = 3000;
    }

    window.setTimeout(function () {
        $(tipId).fadeOut(500);
    },(showTime-500));
}

/*
个人中心
 */
function toAccount(name) {
    window.location.href = '/account/' + name;
}

/*
提交回复
 */
function post() {
    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();
    comment2target(questionId, 1, content);
}

function comment2target(targetId, type, content) {
    if (!content) {
        alert("不能回复空内容");
        return;
    }
    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: "application/json",
        data: JSON.stringify({
            "parentId": targetId,
            "content": content,
            "type": type
        }),
        success: function (response) {
            if (response.code === 200) {
                $("#comment_section").hide();
                window.location.reload();
            } else {
                if (response.code === 2003) {
                    var isAccepted = confirm(response.message);
                    if (isAccepted) {
                        window.open("https://github.com/login/oauth/authorize?client_id=7850db15468cc2a16d28&redirect_uri=http://localhost:8080/callback&scope=user&state=1");
                        window.localStorage.setItem("closable", "true");
                    }
                } else {
                    alert(response.message);
                }
            }
        },
        dataType: "json"
    });
}

function comment(e) {
    var commentId = e.getAttribute("data-id");
    var content = $("#input-" + commentId).val();
    comment2target(commentId, 2, content);
}

//日期格式化
Date.prototype.Format = function(fmt)
{ //author: meizz
    var o = {
        "M+" : this.getMonth()+1,                 //月份
        "d+" : this.getDate(),                    //日
        "h+" : this.getHours(),                   //小时
        "m+" : this.getMinutes(),                 //分
        "s+" : this.getSeconds(),                 //秒
        "q+" : Math.floor((this.getMonth()+3)/3), //季度
        "S"  : this.getMilliseconds()             //毫秒
    };
    if(/(y+)/.test(fmt))
        fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
    for(var k in o)
        if(new RegExp("("+ k +")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length===1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
    return fmt;
};


/*
展开二级评论
 */

function collapseComments(e) {
    var id = e.getAttribute("data-id");
    var comments = $("#comment-" + id);
    var collapse = e.getAttribute("data-collapse");//获取当前折叠状态
    if (collapse) {
        //折叠二级评论
        comments.removeClass("in");
        e.removeAttribute("data-collapse");
        e.classList.remove("active");
    } else {
        var subCommentContainer = $("#comment-" + id);
        if(subCommentContainer.children().length!==1){
            comments.addClass("in");//展开二级评论
            e.setAttribute("data-collapse", "in");//标记状态
            e.classList.add("active");
        }else {
            //追加二级评论
            $.getJSON("/comment/" + id, function (data) {
                $.each(data.data, function (index, comment) {
                    var mediaLeft = $("<div/>",{
                        "class":"media-left"
                    }).append($("<img/>",{
                        "class":"img-rounded media-object",
                        "src":comment.user.avatarUrl
                    }));

                    var bodyElement = $("<div/>",{
                        "class":"media-body"
                    }).append($("<h5/>",{
                        "class":"media-heading",
                        "id":"secComment-"+comment.id,
                        html:comment.user.name
                    })).append($("<div/>",{
                        html:comment.content
                    })).append($("<div/>",{
                        "class":"menu",
                    }).append($("<div/>",{
                        "class":"pull-right",
                        html:new Date(comment.gmtCreate).Format("yyyy-MM-dd hh:mm")+" <a style='cursor: pointer;' onclick='commentToCommentator("+id+","+comment.id+")'>回复</a>"
                    })));

                    var mediaElement = $("<div/>",{
                        "class":"media",
                    }).append(mediaLeft).append(bodyElement);

                    var commentElement = $("<div/>", {
                        "class": "media col-lg-12 col-md-12 col-sm-12 col-xs-12 comments",
                    }).append(mediaElement);

                    subCommentContainer.prepend(commentElement);
                });
                comments.addClass("in");//展开二级评论
                e.setAttribute("data-collapse", "in");//标记状态
                e.classList.add("active");
            });
        }
    }
}

//二级回复
function commentToCommentator(id,commentId) {
    console.log(id);
    var inputBtn = $("#input-"+id);
    var name = $("#secComment-"+commentId).html();
    inputBtn.attr("value","回复 "+name+" :")
}

//ajax.post
function requestAjax(t,method,url, str, val) {
    var channelUrl = url;
    var json = "";
    if(method == "get"){
        url += "?" + toGetStr(str,val);
    }else{
        json = toJson(str,val);
    }
    $.ajax({
        url: url,
        type: method,
        dataType: "json",
        contentType: "application/json",
        data: json,
        success: function (rel) {
            if (rel.code == 200) {
                showTip(rel.message,"success");
            }else{
                showTip(rel.message,"error");
            };
            if(t != null)
                channelSetting(t,rel,channelUrl);
        }
    });
}

function toJson(str,val) {
    str = ""+str;
    val = ""+val;
    var strArray = str.split(",");
    var valArray = val.split(",");
    var json = "{";
    for(k in strArray){
        json += "'"+ strArray[k] +"':"+valArray[k]+",";
    }
    json.substr(0,json.length - 1);
    json = json += "}";
    return json;
}

function toGetStr(str,val) {
    str = ""+str;
    val = ""+val;
    var strArray = str.split(",");
    var valArray = val.split(",");
    var url = "";
    for(k in strArray){
        url += strArray[k]+"="+valArray[k]+"&";
    }
    url = url.substr(0,url.length - 1);
    return url;
}

/**
 * 接口请求成功后的页面补充效果（比如点赞后页面数值+1）
 * @param t
 * @param rel
 * @param url
 */
function channelSetting(t,rel,url) {

    //点赞
    if(url == "/likePost"){
        var count = $(t).text();
        if(rel.code == 200){
            $(t).text(parseInt(count)+1);
        }else{
            $(t).text(parseInt(count)-1);
        }
    }

    //删除帖子刷新页面
    if(rel.code == 200 && url == '/question/delete'){
        window.location.reload();
    }
}

/**
 * 信息窗口
 * @param t
 * @param title
 * @param tip
 */
function setTipWindos(t,title,tip) {

    var url = $(t).data('url');
    var key = $(t).data('key');
    var val = $(t).data('val');

    $("#info_win").show();

    if(t > 0){
        $('#info_but').val(t);
    }else{
        $('#info_but').val($(t).val());
    }

    $('#myModalLabel').html(title);
    $('#myModalContent').html(tip);

   if(url != null) $('#info_but').data('url',url);
   if(key != null) $('#info_but').data('key',key);
   if(val != null) $('#info_but').data('val',val);
}

/**
 * 信息窗口确定操作
 * @param t
 */
function winByDetermine(t) {
    var url = $(t).data('url');
    var key = $(t).data('key');
    var val = $(t).data('val');
    requestAjax(t,'get',url, key, val);
}

// <div class="panel panel-default">
//     <div class="panel-heading">admin说：</div>
// <div class="panel-body">
//     <img src="http://localhost:8080/js/kindeditor/plugins/emoticons/images/18.gif" border="0"> ​​
//                         这个地图要TJ了！！！
// </div>
// </div>
function addMessage(t,rel) {
    var id = $(t).id;
    addMessage(id,rel);
}
function addMessageById(id,rel) {
    var t = $("#"+id);
    var divId = "";
    var html = "<div "+divId+" class=\"panel panel-default\">";
        html+= "<div class=\"panel-heading\">";
        html+= rel.sender+"说：";
        html+= "</div>";
        html+= "<div class=\"panel-body\">";
        html+= rel.content;
        html+= "</div>";
        html+= "</div>";

        t.html(t.html() + html);
}

//{"code":200,"message":"操作成功","data":[{"id":1,"content":"测试一下哈哈~","sender":"管理员","gmt_create":0,"creator":0}]}
function getMessage(id){
    //刷新吐槽
    $.ajax({
        url:"/message/list",
        type : 'GET',
        dataType : 'json',
        timeout : 500000,
        success:function (rel) {
            if(rel.code == 200){
                $("#"+id).html("");
                var list = rel.data;
                for(k in list){
                    addMessageById(id,list[k]);
                }
            }
        }
    });
}

function sendMessage(id) {
    var url = "/message/send";
    var fromId = "#sendMessageForm";
    var from = $('#description')[0];

    editor.sync();
    html=document.getElementById('description').value;//原生API
    $("#schtmlnr").val(html);//把KindEditor产生的baihtml代码放到schtmlnr里面，用于提交

    if(editor.count('text') > 100){
        showTip("字数超过限制，请适当删除部分内容","error");
        return;
    }

    $.ajax({
        url: url ,
        type: "POST",
        dataType: "json",
        data: $(fromId).serialize(),
        success: function (rel) {
            if (rel.code == 200) {
                getMessage(id);
                editor.html("");
                showTip(rel.message,"info");
            }else{
                showTip(rel.message,"error");
            };
        }
    });

}