module CountMoney (
    duration, start, hour,bill
);
input[7:0] duration, start, hour;
output [7:0] bill;
//方便测试时价换成�?10s?
assign bill = start + ((duration/ 'd10) * hour);
endmodule