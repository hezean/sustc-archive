`timescale 1ns/1ps

module mux_74151(
    input mux_en,
    input s2, s1, s0,
    input d0, d1, d2, d3, d4, d5, d6, d7,
    output reg Y,
    output W
);
    always @ * begin
        if (!mux_en) begin
            case ({s2, s1, s0})
                'd0: Y <= d0;
                'd1: Y <= d1;
                'd2: Y <= d2;
                'd3: Y <= d3;
                'd4: Y <= d4;
                'd5: Y <= d5;
                'd6: Y <= d6;
                'd7: Y <= d7;
            endcase
        end
        else begin
            Y <= 'b0;
        end
    end

    assign W = ~Y;
endmodule


module func_df(
    input A, B, C, D,
    output Y
);
    assign Y = (~A & ~B & ~C & ~D) | (B & D) | (A & C) | (~B & C & ~D) | (~A & ~B & ~D);
endmodule


module func_1mux(
    input A, B, C, D,
    output Y
);
    mux_74151 mux(0, A, B, C,
                  ~D, ~D, D, D, 0, 1, D, 1,
                  Y);  // we don't use <W>, aka. ~Y
endmodule


module func_2mux(
    input A, B, C, D,
    output reg Y
);
    reg en1 = 1;
    reg en2 = 1;
    wire y1, y2;

    always @ * begin
        if (D) begin
            en1 <= 0;
            en2 <= 1;
            Y <= y1;
        end
        else begin
            en1 <= 1;
            en2 <= 0;
            Y <= y2;
        end
    end

    mux_74151 mux1(en1, A, B, C,
                   0, 0, 1, 1, 0, 1, 1, 1,
                   y1);  // D == 1

    mux_74151 mux2(en2, A, B, C,
                   1, 1, 0, 0, 0, 1, 0, 1,
                   y2);  // D == 0
endmodule
