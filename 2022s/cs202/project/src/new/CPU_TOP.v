`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 2022/05/20 22:35:13
// Design Name: 
// Module Name: CPU_TOP
// Project Name: 
// Target Devices: 
// Tool Versions: 
// Description: 
// 
// Dependencies: 
// 
// Revision:
// Revision 0.01 - File Created
// Additional Comments:
// 
//////////////////////////////////////////////////////////////////////////////////


module CPU_TOP (clk, fpga_rst, switches, leds, start_pg, rx, tx, button, DIG, Y);
    input clk, fpga_rst; //rst
    input[23:0] switches;
    output[23:0] leds;
    input button;
    output[7:0] DIG;
    output[7:0] Y;
   
   input start_pg;
   input rx;
   output tx;

    wire cpu_clk;
    wire Branch, nBranch, Jmp, Jal, Jr, Zero, RegWrite, RegDst;
    wire[31:0] Instruction;
    wire[31:0] Addr_Result;
    wire[31:0] Read_data_1;
    wire[31:0] Read_data_2;
    wire[31:0] read_data;
    wire[31:0] link_addr;
    wire[31:0] branch_addr;
    wire[31:0] Sign_extend;

    wire ALUSrc;
    wire MemorIOtoReg;
    wire MemRead;
    wire MemWrite;
    wire IORead;
    wire IOWrite;
    wire I_format;
    wire Sftmd;
    wire Comp;
    wire[1:0] ALUOp;
    wire[31:0] ALU_Result;
    wire[31:0] read_from_mem;
    wire[31:0] address;
    wire[31:0] write_data;
    wire[15:0] ioread_data;
    wire[15:0] HI;

    wire        switchcs;
    wire [1:0]  switchaddr; 
    wire [15:0] switchrdata; 
    wire [23:0] switch_i;

    wire        led;
    wire [1:0]  ledaddr;
    wire [15:0] ledwdata;
    
    wire lightcs;
    wire buttoncs;
    wire buttonrdata;
    
   //assign leds[19:0] = {IORead, switchcs, address[1:0], switchrdata[15:0]};
   
   // UART Programmer Pinouts 
   wire upg_clk, upg_clk_o; 
   wire upg_wen_o; //Uart write out enable 
   wire upg_done_o; //Uart rx data have done 
   
   //data to which memory unit of program_rom/dmemory32 
   wire [14:0] upg_adr_o; 
   
   //data to program_rom or dmemory32 
   wire [31:0] upg_dat_o;
   
   wire spg_bufg; 
   BUFG U1(.I(start_pg), .O(spg_bufg)); // de-twitter 
   // Generate UART Programmer reset signal 
   reg upg_rst;
   always @ (posedge clk) begin 
       if (spg_bufg) upg_rst = 0; 
       if (fpga_rst) upg_rst = 1; 
   end //used for other modules which don't relate to UART 
   wire rst; 
   assign rst = fpga_rst | !upg_rst;
   
   uart_bmpg_0 uart(
         .upg_clk_i(upg_clk),
         .upg_rst_i(upg_rst),
         .upg_rx_i(rx),
         .upg_clk_o(upg_clk_o),
         .upg_wen_o(upg_wen_o),
         .upg_adr_o(upg_adr_o),
         .upg_dat_o(upg_dat_o),
         .upg_done_o(upg_done_o),
         .upg_tx_o(tx)
   );

    control32 control(
        .Opcode(Instruction[31:26]),
        .Function_opcode(Instruction[5:0]),
        .Alu_resultHigh(ALU_Result[31:10]),
        .Branch(Branch),
        .nBranch(nBranch),
        .Jr(Jr),
        .Jmp(Jmp),
        .Jal(Jal),
        .ALUSrc(ALUSrc),
        .ALUOp(ALUOp),
        .MemWrite(MemWrite),
        .MemRead(MemRead),
        .IORead(IORead),
        .IOWrite(IOWrite),
        .RegWrite(RegWrite),
        .RegDST(RegDst),
        .MemorIOtoReg(MemorIOtoReg),
        .I_format(I_format),
        .Sftmd(Sftmd)
    );

//    dmemory32 dmem(
//        .clock(cpu_clk),
//        .memWrite(MemWrite),
//        .address(address),
//        .writeData(write_data),
//        .readData(read_from_mem)
//    );
    dmemory32 dmem(
          .ram_clk_i(cpu_clk),
          .ram_wen_i(MemWrite),
          .ram_adr_i(ALU_Result[15:2]),
          .ram_dat_i(Read_data_2),
          .ram_dat_o(read_from_mem),
          .upg_rst_i(upg_rst),
          .upg_clk_i(upg_clk_o),
          .upg_wen_i(upg_wen_o & upg_adr_o[14]),
          .upg_adr_i(upg_adr_o[13:0]),
          .upg_dat_i(upg_dat_o),
          .upg_done_i(upg_done_o)
    );

    executs32 alu(
        .Read_data_1(Read_data_1),
        .Read_data_2(Read_data_2),
        .Sign_extend(Sign_extend),
        .Function_opcode(Instruction[5:0]),
        .Exe_opcode(Instruction[31:26]),
        .ALUOp(ALUOp),
        .Shamt(Instruction[10:6]),
        .ALUSrc(ALUSrc),
        .I_format(I_format),
        .Zero(Zero),
        .Comp(Comp),
        .Jr(Jr),
        .Sftmd(Sftmd),
        .ALU_Result(ALU_Result),
        .Addr_Result(Addr_Result),
        .PC_plus_4(branch_addr),
        .HI(HI)
    );

    decode32 decoder(
        .read_data_1(Read_data_1),
        .read_data_2(Read_data_2),
        .Instruction(Instruction),
        .mem_data(read_data),
        .ALU_result(ALU_Result),
        .Jal(Jal),
        .RegWrite(RegWrite),
        .MemtoReg(MemorIOtoReg),
        .RegDst(RegDst),
        .ALU_Hi(HI),
        .Sign_extend(Sign_extend),
        .clock(cpu_clk),
        .reset(rst),
        .opcplus4(link_addr)
    );
    
    wire[13:0] addr_tmp; // programrom??ifetch?
    wire[31:0] instr_tmp;
    
    programrom prgrom(
        .rom_clk_i(cpu_clk),
        .rom_adr_i(addr_tmp),
        .upg_rst_i(upg_rst),
        .upg_clk_i(upg_clk_o),
        .upg_wen_i(upg_wen_o & (!upg_adr_o[14])),
        .upg_adr_i(upg_adr_o[13:0]),
        .upg_dat_i(upg_dat_o),
        .upg_done_i(upg_done_o),
        .Instruction_o(instr_tmp)
    );
    
    Ifetc32 Ifetc(
        .Instruction_i(instr_tmp), // instruction
        .branch_base_addr(branch_addr),
        .Addr_result(Addr_Result),
        .Read_data_1(Read_data_1),
        .Branch(Branch),
        .nBranch(nBranch),
        .Jmp(Jmp),
        .Jal(Jal),
        .Jr(Jr),
        .Comp(Comp)
        .Zero(Zero),
        .clock(cpu_clk),
        .reset(rst),
        .link_addr(link_addr),
        .rom_adr_o(addr_tmp), //
        .Instruction_o(Instruction)//
    );

    MemOrIO memorio(
        .mRead(MemRead),
        .mWrite(MemWrite),
        .ioRead(IORead),
        .ioWrite(IOWrite),
        .addr_in(ALU_Result),
        .addr_out(address),
        .m_rdata(read_from_mem),
        .io_rdata(ioread_data),
        .r_wdata(read_data),
        .r_rdata(Read_data_2),
        .write_data(write_data),
        .LEDCtrl(led),
        .LightCtrl(lightcs),
        .SwitchCtrl(switchcs),
        .ButtonCtrl(buttoncs)
    );

    assign ioread_data = (buttoncs) ? {15'b0, buttonrdata} : switchrdata;

    LED Led(
        .clk(cpu_clk),
        .rst(rst),
        .ledwrite(IOWrite),
        .led(led),
        .ledaddr(address[1:0]),
        .ledwdata(write_data[15:0]),
        .ledout(leds)
    );

    Switch switch(
        .clk(cpu_clk),
        .rst(rst),
        .switchread(IORead),
        .switch(switchcs),
        .switchaddr(address[1:0]),
        .switchrdata(switchrdata),
        .switch_i(switches)
    );

   cpuclk clk1( 
        .clk_in1(clk), 
        .clk_out1(cpu_clk) ,
        .clk_out2(upg_clk) //
   );
   
   ButtonVibration bt(
    .clk(clk),
    .rst(rst),
    .buttoncs(buttoncs),
    .button(button),
    .buttonout(buttonrdata)
   );

   Light light(//0xFFFF_FC50
        .clk(clk),
        .rst(rst),
        .lightwrite(IOWrite),
        .light(lightcs),
        .lightwdata(write_data[15:0]),
        .DIG(DIG),
        .Y(Y)
    );
endmodule
