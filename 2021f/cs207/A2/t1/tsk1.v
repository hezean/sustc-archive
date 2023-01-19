`timescale 1ns / 1ps

module seg_tube (
    input [3:0] id,
    output reg [7:0] seg_out
);
    always @* begin
        case(id)
            0: seg_out=8'b1100_0000;          // 0
            1: seg_out=8'b1111_1001;          // 1
            2: seg_out=8'b1010_0100;          // 2
            3: seg_out=8'b1011_0000;          // 3
            default: seg_out = 8'b1111_1111;  // off
        endcase
    end
endmodule

module mux (
    input [3:0] bell,
    input hi_fst,
    output reg [7:0] seg_out, seg_en
);
    assign seg_en = 8'b0000_0001;
    reg [3:0] dispid;
    always @* begin
        case(hi_fst)
            1: begin
                casex(bell)
                    'b0000: dispid <= 'b100;  // invalid flag
                    'b0001: dispid <= 0;
                    'b001x: dispid <= 1;
                    'b01xx: dispid <= 2;
                    'b1xxx: dispid <= 3;
                endcase
            end
            0: begin
                casex(bell)
                    'b0000: dispid <= 'b100;
                    'b1000: dispid <= 3;
                    'bx100: dispid <= 2;
                    'bxx10: dispid <= 1;
                    'bxxx1: dispid <= 0;
            end
    end
    seg_tube st(.id(dispid), .seg_out(seg_out));
endmodule