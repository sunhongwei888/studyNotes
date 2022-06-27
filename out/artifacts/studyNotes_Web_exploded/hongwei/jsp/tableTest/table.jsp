<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>表格</title>
    <link rel="stylesheet" href="../../css/table.css" type="text/css">
    <script type="text/javascript" src="../../js/jquery-3.6.0.js"></script>
    <script type="text/javascript" src="../../js/tableTest/table.js"></script>

</head>
<body>
<table id="table">
    <thead>
        <td>序号</td>
        <td>工作任务来源(填写“三定规定 ”或“其他工作”）</td>
        <td>处室职能</td>
        <td>具体职责事项</td>
        <td>一级指标（请下拉选择）</td>
        <td>二级指标（请下拉选择）</td>
        <td>量化指标（一般为“项”“件”“次”等）</td>
        <td>2018年指标数量</td>
        <td>2019年指标数量</td>
        <td>2020年指标数量</td>
        <td style="width:250px">操作</td>
    </thead>
    <tbody id="sanDing">
        <tr>
            <td>1</td>
            <td>三定职责</td>
            <td rowspan="1"><input type="text"></td>
            <td><input type="text"></td>
            <td>
                <select>
                    <option>请选择</option>
                    <option>1</option>
                    <option>2</option>
                    <option>3</option>
                </select>
            </td>
            <td>
                <select>
                    <option>请选择</option>
                    <option>1-1</option>
                    <option>1-2</option>
                    <option>1-3</option>
                </select>
            </td>
            <td><input type="text"></td>
            <td><input type="text"></td>
            <td><input type="text"></td>
            <td><input type="text"></td>
            <td style="text-align:right !important;">
                <button onclick="add(this,'cszn','sanDing')">添加处室职能</button>
                <button onclick="add(this,'zzsx','sanDing')">添加职责事项</button>
                <button onclick="del(this,'sanDing')">删除</button>
            </td>
        </tr>
    </tbody>
    <tbody id="qiTa">
        <tr>
            <td>1</td>
            <td>其他工作</td>
            <td rowspan="1"><input type="text"></td>
            <td><input type="text"></td>
            <td>
                <select>
                    <option>请选择</option>
                    <option>1</option>
                    <option>2</option>
                    <option>3</option>
                </select>
            </td>
            <td>
                <select>
                    <option>请选择</option>
                    <option>1-1</option>
                    <option>1-2</option>
                    <option>1-3</option>
                </select>
            </td>
            <td><input type="text"></td>
            <td><input type="text"></td>
            <td><input type="text"></td>
            <td><input type="text"></td>
            <td style="text-align:right !important;">
                <button onclick="add(this,'cszn','qiTa')">添加处室职能</button>
                <button onclick="add(this,'zzsx','qiTa')">添加职责事项</button>
                <button onclick="del(this,'qiTa')">删除</button>
            </td>
        </tr>
    </tbody>
</table>
</body>
</html>
