`timescale 1ns/1ps

module tsk2tb ();
    reg a, b, c;
    wire sop, pos;

    task2 t(a, b, c, sop, pos);

    initial begin
        {a, b, c} = 3'b000;
        while ({a, b, c} < 3'b111)
            #10 {a, b, c} = {a, b, c} + 1;
        #10 $finish();
    end
endmodule
