/**
 * 添加
 * @param thisTd  当前单元格
 * @param type    两种类型：cszn（处室职能），zzsx（职责事项）
 * @param rwly    任务来源：sanDing（三定职责），qiTa（其他工作）
 */
function add(thisTd,type,rwly) {
  //在下一行生成一行数据
  createRows(thisTd,type,rwly);

  //td合并，其他td隐藏，并重新排序
  mergeRows(type,rwly);

  //禁用添加处室职能
  disabledAdd(rwly);
}


//删除
function del(thisTd,tbody) {

}

/**
 * 生成一行新数据
 * @param thisTd  当前单元格
 * @param type    两种类型：cszn（处室职能），zzsx（职责事项）
 * @param rwly    任务来源：sanDing（三定职责），qiTa（其他工作）
 */
function createRows(thisTd,type,rwly) {
  //拼接一行数据
  var tr =
      '<tr>                                                                '+
      ' <td></td>                                                         '+
      ' <td></td>                                                      ';
  //
  if(type == "cszn"){
    tr = tr + '<td rowspan="1"><input type="text"></td>';
  }else{
    tr = tr + '<td><input type="text"></td>';
  }
  tr = tr + '<td><input type="text"></td>                                       '+
      ' <td>                                                               '+
      '   <select>                                                         '+
      '     <option>请选择</option>                                             '+
      '     <option>1</option>                                             '+
      '     <option>2</option>                                             '+
      '     <option>3</option>                                             '+
      '   </select>                                                        '+
      ' </td>                                                              '+
      ' <td>                                                               '+
      '   <select>                                                         '+
      '     <option>请选择</option>                                           '+
      '     <option>1-1</option>                                           '+
      '     <option>1-2</option>                                           '+
      '     <option>1-3</option>                                           '+
      '   </select>                                                        '+
      ' </td>                                                              '+
      ' <td><input type="text"></td>                                       '+
      ' <td><input type="text"></td>                                       '+
      ' <td><input type="text"></td>                                       '+
      ' <td><input type="text"></td>               '+
      ' <td style="text-align:right !important;">                      '+
      '   <button onClick="add(this,\'cszn\',\''+rwly+'\')">添加处室职能</button>'+
      '   <button onClick="add(this,\'zzsx\',\''+rwly+'\')">添加职责事项</button>'+
      '   <button onClick="del(this,\''+rwly+'\')">删除</button>           '+
      ' </td>                                 '+
      '</tr>';
  //添加至对应位置
  $(thisTd).parent().parent().after(tr);
}

/**
 * 合并td
 * @param type    两种类型：cszn（处室职能），zzsx（职责事项）
 * @param rwly    任务来源：sanDing（三定职责），qiTa（其他工作）
 */
function mergeRows(type,rwly) {
  //获取最大行数，即合并数
  var rowspan = $("#"+rwly+" tr").length;
  var num = 1;
  $("#"+rwly+" tr").each(function (index1) {
    //序号重新排序
    $($(this).find("td")[0]).text(num++);
    //任务来源合并
    if(index1 == 0){
      $($(this).find("td")[1]).attr("rowspan",rowspan);
    }else{
      $($(this).find("td")[1]).hide();
    }

    //处室职能合并
    if(type == "zzsx"){
      var csznRowspan= $($(this).find("td")[2]).attr("rowspan");
      if(csznRowspan != undefined){
        var newCsznRowspan = 1;
        $("#"+rwly+" tr").each(function (index2) {
          if(index2>index1){
            if($($(this).find("td")[2]).attr("rowspan") == undefined){
              newCsznRowspan++;
            }else{
              return false;
            }
          }
        })
        $($(this).find("td")[2]).attr("rowspan",newCsznRowspan);
      }else{
        $($(this).find("td")[2]).hide();
      }
    }
  })
 }

/**
 * 禁用添加处室职能按钮
 * @param rwly    任务来源：sanDing（三定职责），qiTa（其他工作）
 */
 function disabledAdd(rwly) {
  $("#"+rwly+" tr").each(function (index) {
    var rowspan = $($(this).find("td")[2]).attr("rowspan") ;
    if(rowspan > 1){
      for(var i=1;i<rowspan;i++){
        $($("#"+rwly+" tr")[i+index-1]).find("button").first().hide();
      }
      //$($("#"+rwly+" tr")[index+rowspan-2]).find("button").first().attr("disabled",false);
    }
  })
 }

$(document).ready(function (){


})