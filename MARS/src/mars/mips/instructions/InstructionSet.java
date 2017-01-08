   package mars.mips.instructions;
   import mars.simulator.*;
   import mars.mips.hardware.*;
   import mars.mips.instructions.syscalls.*;
   import mars.*;
   import mars.util.*;
   import java.util.*;
   import java.io.*;
	
	/*
Copyright (c) 2003-2013,  Pete Sanderson and Kenneth Vollmar

Developed by Pete Sanderson (psanderson@otterbein.edu)
and Kenneth Vollmar (kenvollmar@missouristate.edu)

Permission is hereby granted, free of charge, to any person obtaining 
a copy of this software and associated documentation files (the 
"Software"), to deal in the Software without restriction, including 
without limitation the rights to use, copy, modify, merge, publish, 
distribute, sublicense, and/or sell copies of the Software, and to 
permit persons to whom the Software is furnished to do so, subject 
to the following conditions:

The above copyright notice and this permission notice shall be 
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR 
ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

(MIT license, http://www.opensource.org/licenses/mit-license.html)
 */

/**
 * The list of Instruction objects, each of which represents a MIPS instruction.
 * The instruction may either be basic (translates into binary machine code) or
 * extended (translates into sequence of one or more basic instructions).
 *
 * @author Pete Sanderson and Ken Vollmar
 * @version August 2003-5
 */

    public class InstructionSet
   {
      private ArrayList instructionList;
	  private ArrayList opcodeMatchMaps;
      private SyscallLoader syscallLoader;
    /**
     * Creates a new InstructionSet object.
     */
       public InstructionSet()
      {
         instructionList = new ArrayList();
      
      }
    /**
     * Retrieve the current instruction set.
     */
       public ArrayList getInstructionList()
      {
         return instructionList;
      
      }
    /**
     * Adds all instructions to the set.  A given extended instruction may have
     * more than one Instruction object, depending on how many formats it can have.
     * @see Instruction
     * @see BasicInstruction
     * @see ExtendedInstruction
     */
       public void populate()
      {
        /* Here is where the parade begins.  Every instruction is added to the set here.*/
      
        // ////////////////////////////////////   BASIC INSTRUCTIONS START HERE ////////////////////////////////
      
         instructionList.add(
                new BasicInstruction("nop",
            	 "Null operation : machine code is all zeroes",
                BasicInstructionFormat.R0_FORMAT,
                "000000 00000 00000 00000 00000 000000",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                  	// Hey I like this so far!
                  }
               }));
         instructionList.add(
                new BasicInstruction("add $t1 = $t2 , $t3",
            	 "Addition : set $t1 to ($t2 plus $t3)",
                BasicInstructionFormat.R3_FORMAT,
                "000000 fffff sssss 0001 ttttt 00000 00",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     int add1 = RegisterFile.getValue(operands[1]);
                     int add2 = RegisterFile.getValue(operands[2]);
                     int sum = add1 + add2;
                  // overflow on A+B detected when A and B have same sign and A+B has other sign.
                     if ((add1 >= 0 && add2 >= 0 && sum < 0)
                        || (add1 < 0 && add2 < 0 && sum >= 0))
                     {
                        throw new ProcessingException(statement,
                            "arithmetic overflow",Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                     }
                     RegisterFile.updateRegister(operands[0], sum);
                  }
               }));
         instructionList.add(
                new BasicInstruction("subf $t1 = $t2 , $t3",
            	 "Subtraction  : set $t1 to ($t3 minus $t2)",
                BasicInstructionFormat.R3_FORMAT,
                "000000 fffff sssss 0010 ttttt 00000 00",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     int sub1 = RegisterFile.getValue(operands[1]);
                     int sub2 = RegisterFile.getValue(operands[2]);
                     int dif = sub2 - sub1  ;
                  // overflow on A-B detected when A and B have opposite signs and A-B has B's sign
                     if ((sub1 >= 0 && sub2 < 0 && dif < 0)
                        || (sub1 < 0 && sub2 >= 0 && dif >= 0))
                     {
                        throw new ProcessingException(statement,
                            "arithmetic overflow",Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                     }
                     RegisterFile.updateRegister(operands[0], dif);
                  }
               }));
         instructionList.add(
                 new BasicInstruction("eq $t1 = $t2 , $t3",
             	 " set $t1 to ($t2 == $t3)",
                 BasicInstructionFormat.R3_FORMAT,
                 "000000 fffff sssss 1011 ttttt 00000 00",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = RegisterFile.getValue(operands[2]);
                      boolean res = sub1 == sub2;
                      if(res)
                    	  RegisterFile.updateRegister(operands[0], 1);
                      else
                    	  RegisterFile.updateRegister(operands[0], 0);
                   }
                }));
         instructionList.add(
                 new BasicInstruction("eq $t1 = $t2 , 100",
             	 " set $t1 to ($t2 == imm12)",
             	BasicInstructionFormat.I_FORMAT,
                "000100 fffff sssss 0000 tttttttttttt",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = operands[2];
                      
                      boolean res = sub1 == sub2;
                      if(res)
                    	  RegisterFile.updateRegister(operands[0], 1);
                      else
                    	  RegisterFile.updateRegister(operands[0], 0);
                   }
                }));
         instructionList.add(
                 new BasicInstruction("ne $t1 = $t2 , $t3",
             	 " set $t1 to ($t2 != $t3)",
                 BasicInstructionFormat.R3_FORMAT,
                 "000000 fffff sssss 1100 ttttt 00000 00",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = RegisterFile.getValue(operands[2]);
                      boolean res = sub1 != sub2;
                      if(res)
                    	  RegisterFile.updateRegister(operands[0], 0);
                      else
                    	  RegisterFile.updateRegister(operands[0], 1);
                   }
                }));
         instructionList.add(
                 new BasicInstruction("ne $t1 = $t2 , 100",
             	 " set $t1 to ($t2 != imm12)",
             	BasicInstructionFormat.I_FORMAT,
                "000100 fffff sssss 0001 tttttttttttt",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = operands[2];
                      boolean res = sub1 != sub2;
                      if(res)
                    	  RegisterFile.updateRegister(operands[0], 0);
                      else
                    	  RegisterFile.updateRegister(operands[0], 1);
                   }
                }));
         instructionList.add(
                 new BasicInstruction("lt $t1 = $t2 , $t3",
             	 " set $t1 to ($t2 < $t3)",
                 BasicInstructionFormat.R3_FORMAT,
                 "000000 fffff sssss 1101 ttttt 00000 00",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = RegisterFile.getValue(operands[2]);
                      boolean res = sub1 < sub2;
                      if(res)
                    	  RegisterFile.updateRegister(operands[0], 1);
                      else
                    	  RegisterFile.updateRegister(operands[0], 0);
                   }
                }));
         instructionList.add(
                 new BasicInstruction("lt $t1 = $t2 , 100",
             	 " set $t1 to ($t2 < imm12)",
             	BasicInstructionFormat.I_FORMAT,
                "000100 fffff sssss 0010 tttttttttttt",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = operands[2];
                      boolean res = sub1 < sub2;
                      if(res)
                    	  RegisterFile.updateRegister(operands[0], 1);
                      else
                    	  RegisterFile.updateRegister(operands[0], 0);
                   }
                }));
         instructionList.add(
                 new BasicInstruction("ltu $t1 = $t2 , $t3",
             	 " set $t1 to (|$t2| < |$t3|)",
                 BasicInstructionFormat.R3_FORMAT,
                 "000000 fffff sssss 1110 ttttt 00000 00",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = RegisterFile.getValue(operands[2]);
                      boolean res = Math.abs(sub1) < Math.abs(sub2);
                      if(res)
                    	  RegisterFile.updateRegister(operands[0], 1);
                      else
                    	  RegisterFile.updateRegister(operands[0], 0);
                   }
                }));
         instructionList.add(
                 new BasicInstruction("ge $t1 = $t2 , $t3",
             	 " set $t1 to ($t2 >= $t3)",
                 BasicInstructionFormat.R3_FORMAT,
                 "000000 fffff sssss 1111 ttttt 00000 00",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = RegisterFile.getValue(operands[2]);
                      boolean res = sub1 >= sub2;
                      if(res)
                    	  RegisterFile.updateRegister(operands[0], 1);
                      else
                    	  RegisterFile.updateRegister(operands[0], 0);
                   }
                }));
         instructionList.add(
                 new BasicInstruction("ge $t1 = $t2 , 100",
             	 " set $t1 to ($t2 >= $t3)",
             	BasicInstructionFormat.I_FORMAT,
                "000100 fffff sssss 0011 tttttttttttt",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = operands[2];
                      boolean res = sub1 >= sub2;
                      if(res)
                    	  RegisterFile.updateRegister(operands[0], 1);
                      else
                    	  RegisterFile.updateRegister(operands[0], 0);
                   }
                }));
         instructionList.add(
                 new BasicInstruction("geu $t1 = $t2 , $t3",
             	 " set $t1 to (|$t2| >= |$t3|)",
                 BasicInstructionFormat.R3_FORMAT,
                 "000000 fffff sssss 0000 ttttt 00000 01",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = RegisterFile.getValue(operands[2]);
                      boolean res = Math.abs(sub1) >= Math.abs(sub2);
                      if(res)
                    	  RegisterFile.updateRegister(operands[0], 1);
                      else
                    	  RegisterFile.updateRegister(operands[0], 0);
                   }
                }));
         instructionList.add(
                 new BasicInstruction("and_eq $t1 = $t2 , $t3",
             	 "Rd = (Ra == Rb)? Rd : 0",
                 BasicInstructionFormat.R3_FORMAT,
                 "000000 fffff sssss 1011 ttttt 00000 01",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = RegisterFile.getValue(operands[2]);
                      boolean res = sub1 == sub2;
                      if(res)
                    	;  
                      else
                    	  RegisterFile.updateRegister(operands[0], 0);
                   }
                }));
         instructionList.add(
                 new BasicInstruction("or_eq $t1 = $t2 , $t3",
             	 "Rd = (Ra == Rb)? 1 : Rd",
                 BasicInstructionFormat.R3_FORMAT,
                 "000000 fffff sssss 1011 ttttt 00000 10",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = RegisterFile.getValue(operands[2]);
                      boolean res = sub1 == sub2;
                      if(res)
                    	  RegisterFile.updateRegister(operands[0], 1); 
                      else
                    	  ;
                   }
                }));
         instructionList.add(
                 new BasicInstruction("or_eq $t1 = $t2 , 100",
             	 "Rd = (Ra == imm12)? 1 : Rd",
             	BasicInstructionFormat.I_FORMAT,
                "000100 fffff sssss 1000 tttttttttttt",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = operands[2];
                      boolean res = sub1 == sub2;
                      if(res)
                    	  RegisterFile.updateRegister(operands[0], 1); 
                      else
                    	  ;
                   }
                }));
         instructionList.add(
                 new BasicInstruction("or_ne $t1 = $t2 , $t3",
             	 "Rd = (Ra == imm12)? 1 : Rd",
                 BasicInstructionFormat.R3_FORMAT,
                 "000000 fffff sssss 1100 ttttt 00000 10",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = RegisterFile.getValue(operands[2]);
                      boolean res = sub1 != sub2;
                      if(res)
                    	  RegisterFile.updateRegister(operands[0], 1); 
                      else
                    	  ;
                   }
                }));
         instructionList.add(
                 new BasicInstruction("or_ne $t1 = $t2 , 100",
             	 "Rd = (Ra == Rb)? 1 : Rd",
             	BasicInstructionFormat.I_FORMAT,
                "000100 fffff sssss 1001 tttttttttttt",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = operands[2];
                      boolean res = sub1 != sub2;
                      if(res)
                    	  RegisterFile.updateRegister(operands[0], 1); 
                      else
                    	  ;
                   }
                }));
         instructionList.add(
                 new BasicInstruction("and_eq $t1 = $t2 , 100",
             	 "Rd = (Ra == imm12)? Rd : 0",
             	BasicInstructionFormat.I_FORMAT,
                "000100 fffff sssss 0100 tttttttttttt",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = operands[2];
                      boolean res = sub1 == sub2;
                      if(res)
                    	;  
                      else
                    	  RegisterFile.updateRegister(operands[0], 0);
                   }
                }));
         instructionList.add(
                 new BasicInstruction("and_ne $t1 = $t2 , $t3",
             	 "Rd = (Ra != Rb)? Rd : 0",
                 BasicInstructionFormat.R3_FORMAT,
                 "000000 fffff sssss 1100 ttttt 00000 01",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      System.out.println("KKKKKKKKK"+operands[0]);
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = RegisterFile.getValue(operands[2]);
                      boolean res = sub1 != sub2;
                      if(res)
                    	;  
                      else
                    	  RegisterFile.updateRegister(operands[0], 0);
                   }
                }));
         instructionList.add(
                 new BasicInstruction("and_ne $t1 = $t2 , 100",
             	 "Rd = (Ra != imm12)? Rd : 0",
             	BasicInstructionFormat.I_FORMAT,
                "000100 fffff sssss 0101 tttttttttttt",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = operands[2];
                      boolean res = sub1 != sub2;
                      if(res)
                    	;  
                      else
                    	  RegisterFile.updateRegister(operands[0], 0);
                   }
                }));
         instructionList.add(
                 new BasicInstruction("and_lt $t1 = $t2 , $t3",
             	 "Rd = (Ra < Rb)? Rd : 0",
                 BasicInstructionFormat.R3_FORMAT,
                 "000000 fffff sssss 1101 ttttt 00000 01",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = RegisterFile.getValue(operands[2]);
                      boolean res = sub1 < sub2;
                      if(res)
                    	;  
                      else
                    	  RegisterFile.updateRegister(operands[0], 0);
                   }
                }));
         instructionList.add(
                 new BasicInstruction("or_lt $t1 = $t2 , $t3",
             	 "Rd = (Ra < Rb)? 1 : rd",
                 BasicInstructionFormat.R3_FORMAT,
                 "000000 fffff sssss 1101 ttttt 00000 10",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = RegisterFile.getValue(operands[2]);
                      boolean res = sub1 < sub2;
                      if(res)
                    	  RegisterFile.updateRegister(operands[0], 1);  
                      else
                    	  ;
                   }
                }));
         instructionList.add(
                 new BasicInstruction("or_lt $t1 = $t2 , 100",
             	 "Rd = (Ra < imm12)? 1 : rd",
             	BasicInstructionFormat.I_FORMAT,
                "000100 fffff sssss 1010 tttttttttttt",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = operands[2];
                      boolean res = sub1 < sub2;
                      if(res)
                    	  RegisterFile.updateRegister(operands[0], 1);  
                      else
                    	  ;
                   }
                }));
         instructionList.add(
                 new BasicInstruction("and_lt $t1 = $t2 , 100",
             	 "Rd = (Ra < imm12)? Rd : 0",
             	BasicInstructionFormat.I_FORMAT,
                "000100 fffff sssss 0110 tttttttttttt",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = operands[2];
                      boolean res = sub1 < sub2;
                      if(res)
                    	;  
                      else
                    	  RegisterFile.updateRegister(operands[0], 0);
                   }
                }));
         instructionList.add(
                 new BasicInstruction("and_ltu $t1 = $t2 , $t3",
             	 "Rd = (|Ra| < |Rb|)? Rd : 0",
                 BasicInstructionFormat.R3_FORMAT,
                 "000000 fffff sssss 1110 ttttt 00000 01",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = RegisterFile.getValue(operands[2]);
                      boolean res = Math.abs(sub1) < Math.abs(sub2) ;
                      if(res)
                    	;  
                      else
                    	  RegisterFile.updateRegister(operands[0], 0);
                   }
                }));
         instructionList.add(
                 new BasicInstruction("or_ltu $t1 = $t2 , $t3",
             	 "Rd = (|Ra| < |Rb|)? 1 : Rd",
                 BasicInstructionFormat.R3_FORMAT,
                 "000000 fffff sssss 1110 ttttt 00000 10",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = RegisterFile.getValue(operands[2]);
                      boolean res = Math.abs(sub1) < Math.abs(sub2) ;
                      if(res)
                    	  RegisterFile.updateRegister(operands[0], 1);  
                      else
                    	  ;
                   }
                }));
         instructionList.add(
                 new BasicInstruction("and_ltu $t1 = $t2 , $t3",
             	 "Rd = (|Ra| < |Rb|)? Rd : 0",
                 BasicInstructionFormat.R3_FORMAT,
                 "000001 fffff sssss 0000 ttttt 00000 00",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = RegisterFile.getValue(operands[2]);
                      boolean res = Math.abs(sub1) < Math.abs(sub2) ;
                      if(res)
                    	;  
                      else
                    	  RegisterFile.updateRegister(operands[0], 0);
                   }
                }));
         instructionList.add(
                 new BasicInstruction("and_ge $t1 = $t2 , $t3",
             	 "Rd = (Ra > Rb)? Rd : 0",
                 BasicInstructionFormat.R3_FORMAT,
                 "000000 fffff sssss 1110 ttttt 00000 01",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = RegisterFile.getValue(operands[2]);
                      boolean res = sub1 >= sub2 ;
                      if(res)
                    	;  
                      else
                    	  RegisterFile.updateRegister(operands[0], 0);
                   }
                }));
         instructionList.add(
                 new BasicInstruction("or_ge $t1 = $t2 , $t3",
             	 "Rd = (Ra > Rb)? 1 : Rd",
                 BasicInstructionFormat.R3_FORMAT,
                 "000000 fffff sssss 1111 ttttt 00000 10",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = RegisterFile.getValue(operands[2]);
                      boolean res = sub1 >= sub2 ;
                      if(res)
                    	  RegisterFile.updateRegister(operands[0], 1); 
                      else
                    	  ;
                   }
                }));
         instructionList.add(
                 new BasicInstruction("or_ge $t1 = $t2 , 100",
             	 "Rd = (Ra > imm12)? 1 : Rd",
             	BasicInstructionFormat.I_FORMAT,
                "000100 fffff sssss 1011 tttttttttttt",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = operands[2];
                      boolean res = sub1 >= sub2 ;
                      if(res)
                    	  RegisterFile.updateRegister(operands[0], 1); 
                      else
                    	  ;
                   }
                }));
         instructionList.add(
                 new BasicInstruction("and_ge $t1 = $t2 , 100",
             	 "Rd = (Ra > Rb)? Rd : 0",
             	BasicInstructionFormat.I_FORMAT,
                "000100 fffff sssss 0111 tttttttttttt",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = operands[2];
                      boolean res = sub1 >= sub2 ;
                      if(res)
                    	;  
                      else
                    	  RegisterFile.updateRegister(operands[0], 0);
                   }
                }));
         instructionList.add(
                 new BasicInstruction("and_geu $t1 = $t2 , $t3",
             	 "Rd = (|Ra| >= |Rb|)? Rd : 0",
                 BasicInstructionFormat.R3_FORMAT,
                 "000000 fffff sssss 0000 ttttt 00000 10",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = RegisterFile.getValue(operands[2]);
                      boolean res = Math.abs(sub1) >= Math.abs(sub2) ;
                      if(res)
                    	;  
                      else
                    	  RegisterFile.updateRegister(operands[0], 0);
                   }
                }));
         instructionList.add(
                 new BasicInstruction("or_geu $t1 = $t2 , $t3",
             	 "Rd = (|Ra| >= |Rb|)? 1 :Rd",
                 BasicInstructionFormat.R3_FORMAT,
                 "000000 fffff sssss 0000 ttttt 00000 11",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = RegisterFile.getValue(operands[2]);
                      boolean res = Math.abs(sub1) >= Math.abs(sub2) ;
                      if(res)
                    	  RegisterFile.updateRegister(operands[0], 1);  
                      else
                    	  ;
                   }
                }));
         instructionList.add(
                new BasicInstruction("add $t1 = $t2 , -100",
            	 "Addition : set $t1 to ($t2 plus signed 12-bit immediate)",
                BasicInstructionFormat.I_FORMAT,
                "000010 fffff sssss 0000 tttttttttttt",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     int add1 = RegisterFile.getValue(operands[1]);
                     int add2 = operands[2] << 20 >> 20;
                     int sum = add1 + add2;
                  // overflow on A+B detected when A and B have same sign and A+B has other sign.
                     if ((add1 >= 0 && add2 >= 0 && sum < 0)
                        || (add1 < 0 && add2 < 0 && sum >= 0))
                     {
                        throw new ProcessingException(statement,
                            "arithmetic overflow",Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                     }
                     RegisterFile.updateRegister(operands[0], sum);
                  }
               }));
         instructionList.add(
                 new BasicInstruction("subf $t1 = $t2 , -100",
             	 "Subtraction  immediate with overflow : set $t1 to (-$t2 plus signed 12-bit immediate)",
                 BasicInstructionFormat.I_FORMAT,
                 "000010 fffff sssss 0001 tttttttttttt",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = operands[2] << 20 >> 20;
                      int dif = sub2 - sub1;
                   // overflow on A+B detected when A and B have same sign and A+B has other sign.
                      if ((sub1 >= 0 && sub2 < 0 && dif < 0)
                              || (sub1 < 0 && sub2 >= 0 && dif >= 0))
                           {
                              throw new ProcessingException(statement,
                                  "arithmetic overflow",Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                           }
                      RegisterFile.updateRegister(operands[0], dif);
                   }
                }));
         
         instructionList.add(
                new BasicInstruction("mthi $t1", 
            	 "Move to HI registerr : Set HI to contents of $t1 (see multiply and divide operations)",
            	 BasicInstructionFormat.R_FORMAT,
                "000000 fffff 00000 00000 00000 010001",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     RegisterFile.updateRegister(33,
                        RegisterFile.getValue(operands[0]));
                  }
               }));
         instructionList.add(
                new BasicInstruction("mtlo $t1", 
            	 "Move to LO register : Set LO to contents of $t1 (see multiply and divide operations)",
            	 BasicInstructionFormat.R_FORMAT,
                "000000 fffff 00000 00000 00000 010011",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     RegisterFile.updateRegister(34,
                        RegisterFile.getValue(operands[0]));
                  }
               }));
         instructionList.add(
                new BasicInstruction("and $t1 = $t2 , $t3",
            	 "Bitwise AND : Set $t1 to bitwise AND of $t2 and $t3",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff sssss 0011 ttttt 00000 00",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     RegisterFile.updateRegister(operands[0],
                        RegisterFile.getValue(operands[1])
                        & RegisterFile.getValue(operands[2]));
                  }
               }));
         instructionList.add(
                new BasicInstruction("or $t1 = $t2 , $t3",
            	 "Bitwise OR : Set $t1 to bitwise OR of $t2 and $t3",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff sssss 0100 ttttt 00000 00",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     RegisterFile.updateRegister(operands[0],
                        RegisterFile.getValue(operands[1])
                        | RegisterFile.getValue(operands[2]));
                  }
               }));
         instructionList.add(
                new BasicInstruction("and $t1 = $t2 , 100",
            	 "Bitwise AND immediate : Set $t1 to bitwise AND of $t2 and  12-bit immediate",
                BasicInstructionFormat.I_FORMAT,
                "000010 fffff sssss 0010 tttttttttttt",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                  
                     RegisterFile.updateRegister(operands[0],
                        RegisterFile.getValue(operands[1])
                        & (operands[2]));
                  }
               }));
         instructionList.add(
                new BasicInstruction("or $t1 = $t2 , 100",
            	 "Bitwise OR immediate : Set $t1 to bitwise OR of $t2 and 12-bit immediate",
                BasicInstructionFormat.I_FORMAT,
                "000010 fffff sssss 0011 tttttttttttt",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                 
                     RegisterFile.updateRegister(operands[0],
                        RegisterFile.getValue(operands[1])
                        | (operands[2]));
                  }
               }));
         instructionList.add(
                new BasicInstruction("nor $t1 = $t2 , $t3",
            	 "Bitwise NOR : Set $t1 to bitwise NOR of $t2 and $t3",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff sssss 0110 ttttt 00000 00",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     RegisterFile.updateRegister(operands[0],
                        ~(RegisterFile.getValue(operands[1])
                        | RegisterFile.getValue(operands[2])));
                  }
               }));
         instructionList.add(
                 new BasicInstruction("nor $t1 = $t2 , 100",
             	 "Bitwise NOR : Set $t1 to bitwise NOR of $t2 and 12-bit immed",
             	BasicInstructionFormat.I_FORMAT,
                "000010 fffff sssss 0101 tttttttttttt",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      RegisterFile.updateRegister(operands[0],
                         ~(RegisterFile.getValue(operands[1])
                         | RegisterFile.getValue(operands[2])));
                   }
                }));
         instructionList.add(
                new BasicInstruction("xor $t1 = $t2 , $t3",
            	 "Bitwise XOR (exclusive OR) : Set $t1 to bitwise XOR of $t2 and $t3",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff sssss 0101 ttttt 00000 00",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     RegisterFile.updateRegister(operands[0],
                        RegisterFile.getValue(operands[1])
                        ^ RegisterFile.getValue(operands[2]));
                  }
               }));
         instructionList.add(
                new BasicInstruction("xor $t1 = $t2 , 100",
            	 "Bitwise XOR immediate : Set $t1 to bitwise XOR of $t2 and  12-bit immediate",
                BasicInstructionFormat.I_FORMAT,
                "000010 fffff sssss 0100 tttttttttttt",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                  
                     RegisterFile.updateRegister(operands[0],
                        RegisterFile.getValue(operands[1])
                        ^ (operands[2]));
                  }
               }));					
         instructionList.add(
                new BasicInstruction("shl $t1 = $t2,10",
            	 "Shift left logical : Set $t1 to result of shifting $t2 left by number of bits specified by immediate",
            	 BasicInstructionFormat.I_FORMAT,
                 "000010 fffff sssss 0110 tttttttttttt",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     RegisterFile.updateRegister(operands[0],
                        RegisterFile.getValue(operands[1]) << operands[2]);
                  }
               }));
         instructionList.add(
                new BasicInstruction("shl $t1 = $t2 , $t3",
            	 "Shift left logical variable : Set $t1 to result of shifting $t2 left by number of bits specified by value in low-order 5 bits of $t3",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff sssss 0111 ttttt 00000 00",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                  // Mask all but low 5 bits of register containing shamt.
                     RegisterFile.updateRegister(operands[0],
                        RegisterFile.getValue(operands[1]) << 
                        (RegisterFile.getValue(operands[2]) & 0x0000001F));
                  }
               }));
         instructionList.add(
                 new BasicInstruction("ror $t1 = $t2 , $t3",
             	 "rotate right variable : Set $t1 to result of circular rotating $t2 right by number of bits specified by value in $t3",
                 BasicInstructionFormat.R_FORMAT,
                 "000000 fffff sssss 1001 ttttt 00000 00",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                   
                      
                      RegisterFile.updateRegister(operands[0],
                    		  Integer.rotateRight(RegisterFile.getValue(operands[1]), (RegisterFile.getValue(operands[2]))));
                   }
                }));
         instructionList.add(
                 new BasicInstruction("ror $t1 = $t2 , 100",
             	 "rotate right variable : Set $t1 to result of circular rotating $t2 right by number of bits specified by 12-bit immed",
             	BasicInstructionFormat.I_FORMAT,
                "000010 fffff sssss 1001 tttttttttttt",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                   
                      
                      RegisterFile.updateRegister(operands[0],
                    		  Integer.rotateRight(RegisterFile.getValue(operands[1]), (RegisterFile.getValue(operands[2]))));
                   }
                }));
         
         instructionList.add(
                new BasicInstruction("srl $t1 = $t2 , 10",
            	 "Shift right logical : Set $t1 to result of shifting $t2 right by number of bits specified by immediate",
                BasicInstructionFormat.R_FORMAT,
                "000000 00000 sssss fffff ttttt 000010",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                  // must zero-fill, so use ">>>" instead of ">>".
                     RegisterFile.updateRegister(operands[0],
                        RegisterFile.getValue(operands[1]) >>> operands[2]);
                  }
               }));
         instructionList.add(
                new BasicInstruction("sar $t1 = $t2 , 10",
                "Shift right arithmetic : Set $t1 to result of sign-extended shifting $t2 right by number of bits specified by immediate",
                BasicInstructionFormat.I_FORMAT,
                "000010 fffff sssss 1000 tttttttttttt",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                  // must sign-fill, so use ">>".
                     RegisterFile.updateRegister(operands[0],
                        RegisterFile.getValue(operands[1]) >> operands[2]);
                  }
               }));
         instructionList.add(
                new BasicInstruction("sar $t1 = $t2 , $t3",
            	 "Shift right arithmetic variable : Set $t1 to result of sign-extended shifting $t2 right by number of bits specified by value in low-order 5 bits of $t3",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff sssss 1001 ttttt 00000 00",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                  // Mask all but low 5 bits of register containing shamt.Use ">>" to sign-fill.
                     RegisterFile.updateRegister(operands[0],
                        RegisterFile.getValue(operands[1]) >> 
                        (RegisterFile.getValue(operands[2]) & 0x0000001F));
                  }
               }));
         instructionList.add(
                new BasicInstruction("shr $t1 = $t2 , $t3",
            	 "Shift right logical variable : Set $t1 to result of shifting $t2 right by number of bits specified by value in low-order 5 bits of $t3",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff sssss 1000 ttttt 00000 00",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                  // Mask all but low 5 bits of register containing shamt.Use ">>>" to zero-fill.
                     RegisterFile.updateRegister(operands[0],
                        RegisterFile.getValue(operands[1]) >>> 
                        (RegisterFile.getValue(operands[2]) & 0x0000001F));
                  }
               }));
         instructionList.add(
                 new BasicInstruction("shr $t1 = $t2 , 100",
             	 "Shift right logical variable : Set $t1 to result of shifting $t2 right by number of bits specified by value in low-order 5 bits of $t3",
             	BasicInstructionFormat.I_FORMAT,
                "000010 fffff sssss 0111 tttttttttttt",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                    
                      RegisterFile.updateRegister(operands[0],
                         RegisterFile.getValue(operands[1]) >>> 
                         (RegisterFile.getValue(operands[2])));
                   }
                }));
       //Ret_ALU

         instructionList.add(
                new BasicInstruction("ret_and $t1 = $t2 , $t3",
            	 "PC = r31;Bitwise AND : Set $t1 to bitwise AND of $t2 and $t3",
                BasicInstructionFormat.R3_FORMAT,
                "000011 fffff sssss 0100 ttttt 00000 00",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     RegisterFile.updateRegister(operands[0],
                        RegisterFile.getValue(operands[1])
                        & RegisterFile.getValue(operands[2]));
					processJump(31);	
                  }
               }));
		
         instructionList.add(
                new BasicInstruction("ret_shl $t1 = $t2 , $t3",
            	 "PC = r31;Shift left logical variable : Set $t1 to result of shifting $t2 left by number of bits specified by value in low-order 5 bits of $t3",
                BasicInstructionFormat.R3_FORMAT,
                "000011 fffff sssss 1000 ttttt 00000 00",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                  // Mask all but low 5 bits of register containing shamt.
                     RegisterFile.updateRegister(operands[0],
                        RegisterFile.getValue(operands[1]) << 
                        (RegisterFile.getValue(operands[2]) & 0x0000001F));
					processJump(31);
                  }
               }));

         instructionList.add(
                 new BasicInstruction("ret_ror $t1 = $t2 , $t3",
             	 "PC = r31; rotate right variable : Set $t1 to result of circular rotating $t2 right by number of bits specified by value in $t3",
                 BasicInstructionFormat.R3_FORMAT,
                 "000011 fffff sssss 1011 ttttt 00000 00",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                   
                      
                      RegisterFile.updateRegister(operands[0],
                    		  Integer.rotateRight(RegisterFile.getValue(operands[1]), (RegisterFile.getValue(operands[2]))));
						processJump(31);
                   }
                }));
         instructionList.add(
                new BasicInstruction("ret_sar $t1 = $t2 , $t3",
            	 "PC = r31; Shift right arithmetic variable : Set $t1 to result of sign-extended shifting $t2 right by number of bits specified by value in low-order 5 bits of $t3",
                BasicInstructionFormat.R3_FORMAT,
                "000011 fffff sssss 1010 ttttt 00000 00",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                  // Mask all but low 5 bits of register containing shamt.Use ">>" to sign-fill.
                     RegisterFile.updateRegister(operands[0],
                        RegisterFile.getValue(operands[1]) >> 
                        (RegisterFile.getValue(operands[2]) & 0x0000001F));
					processJump(31);
                  }
               }));

         instructionList.add(
                new BasicInstruction("ret_add $t1 = $t2 , $t3",
            	 "PC = r31; Addition with overflow : set $t1 to ($t2 plus $t3)",
                BasicInstructionFormat.R3_FORMAT,
                "000011 fffff sssss 0000 ttttt 00000 00",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     int add1 = RegisterFile.getValue(operands[1]);
                     int add2 = RegisterFile.getValue(operands[2]);
                     int sum = add1 + add2;
                  // overflow on A+B detected when A and B have same sign and A+B has other sign.
                     if ((add1 >= 0 && add2 >= 0 && sum < 0)
                        || (add1 < 0 && add2 < 0 && sum >= 0))
                     {
                        throw new ProcessingException(statement,
                            "arithmetic overflow",Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                     }
                     RegisterFile.updateRegister(operands[0], sum);
					 processJump(31);
                  }
               }));
         instructionList.add(
                new BasicInstruction("ret_subf $t1 = $t2 , $t3",
            	 "PC = r31; Subtraction with overflow : set $t1 to ($t2 minus $t3)",
                BasicInstructionFormat.R3_FORMAT,
                "000011 fffff sssss 0001 ttttt 00000 00",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     int sub1 = RegisterFile.getValue(operands[1]);
                     int sub2 = RegisterFile.getValue(operands[2]);
                     int dif = sub1 - sub2;
                  // overflow on A-B detected when A and B have opposite signs and A-B has B's sign
                     if ((sub1 >= 0 && sub2 < 0 && dif < 0)
                        || (sub1 < 0 && sub2 >= 0 && dif >= 0))
                     {
                        throw new ProcessingException(statement,
                            "arithmetic overflow",Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                     }
                     RegisterFile.updateRegister(operands[0], dif);
					 processJump(31);
                  }
               }));
         instructionList.add(
                new BasicInstruction("ret_or $t1 = $t2 , $t3",
            	 "PC = r31; Bitwise OR : Set $t1 to bitwise OR of $t2 and $t3",
                BasicInstructionFormat.R3_FORMAT,
                "000011 fffff sssss 0101 ttttt 00000 00",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     RegisterFile.updateRegister(operands[0],
                        RegisterFile.getValue(operands[1])
                        | RegisterFile.getValue(operands[2]));
					processJump(31);
                  }
               }));


         instructionList.add(
                new BasicInstruction("ret_nor $t1 = $t2 , $t3",
            	 "PC = r31; Bitwise NOR : Set $t1 to bitwise NOR of $t2 and $t3",
                BasicInstructionFormat.R3_FORMAT,
                "000011 fffff sssss 0111 ttttt 00000 00",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     RegisterFile.updateRegister(operands[0],
                        ~(RegisterFile.getValue(operands[1])
                        | RegisterFile.getValue(operands[2])));
					processJump(31);
                  }
               }));

         instructionList.add(
                new BasicInstruction("ret_xor $t1 = $t2 , $t3",
            	 "PC = r31; Bitwise XOR (exclusive OR) : Set $t1 to bitwise XOR of $t2 and $t3",
                BasicInstructionFormat.R3_FORMAT,
                "000011 fffff sssss 0110 ttttt 00000 00",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     RegisterFile.updateRegister(operands[0],
                        RegisterFile.getValue(operands[1])
                        ^ RegisterFile.getValue(operands[2]));
					processJump(31);
                  }
               }));

			   
         instructionList.add(
                new BasicInstruction("ret_shr $t1 = $t2 , $t3",
            	 "PC = r31; Shift right logical variable : Set $t1 to result of shifting $t2 right by number of bits specified by value in low-order 5 bits of $t3",
                BasicInstructionFormat.R3_FORMAT,
                "000011 fffff sssss 1001 ttttt 00000 00",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                  // Mask all but low 5 bits of register containing shamt.Use ">>>" to zero-fill.
                     RegisterFile.updateRegister(operands[0],
                        RegisterFile.getValue(operands[1]) >>> 
                        (RegisterFile.getValue(operands[2]) & 0x0000001F));
					processJump(31);
                  }
               }));
			   
//RETI_ALUI
         instructionList.add(
                new BasicInstruction("reti_add $t1 = $t2 , -100",
            	 "PC = r31; Addition immediate with overflow : set $t1 to ($t2 plus signed 12-bit immediate)",
                BasicInstructionFormat.I_FORMAT,
                "000111 fffff 0000 sssss tttttttttttt",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     int add1 = RegisterFile.getValue(operands[1]);
                     int add2 = operands[2] << 20 >> 20;
                     int sum = add1 + add2;
                  // overflow on A+B detected when A and B have same sign and A+B has other sign.
                     if ((add1 >= 0 && add2 >= 0 && sum < 0)
                        || (add1 < 0 && add2 < 0 && sum >= 0))
                     {
                        throw new ProcessingException(statement,
                            "arithmetic overflow",Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                     }
                     RegisterFile.updateRegister(operands[0], sum);
					 processJump(31);
                  }
               }));
         instructionList.add(
                 new BasicInstruction("reti_subf $t1 = $t2 , -100",
             	 "PC = r31; Subtraction  immediate with overflow : set $t1 to (-$t2 plus signed 12-bit immediate)",
                 BasicInstructionFormat.I_FORMAT,
                 "000111 fffff 0001 sssss tttttttttttt",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      int sub1 = RegisterFile.getValue(operands[1]);
                      int sub2 = operands[2] << 20 >> 20;
                      int dif = sub2 - sub1;
                   // overflow on A+B detected when A and B have same sign and A+B has other sign.
                      if ((sub1 >= 0 && sub2 < 0 && dif < 0)
                              || (sub1 < 0 && sub2 >= 0 && dif >= 0))
                           {
                              throw new ProcessingException(statement,
                                  "arithmetic overflow",Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                           }
                      RegisterFile.updateRegister(operands[0], dif);
					  processJump(31);
                   }
                }));
         instructionList.add(
                new BasicInstruction("reti_and $t1 = $t2 , 100",
            	 "PC = r31; Bitwise AND immediate : Set $t1 to bitwise AND of $t2 and  12-bit immediate",
                BasicInstructionFormat.I_FORMAT,
                "000111 fffff 0100 sssss tttttttttttt",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                  
                     RegisterFile.updateRegister(operands[0],
                        RegisterFile.getValue(operands[1])
                        & (operands[2]));
					processJump(31);
                  }
               }));
         instructionList.add(
                new BasicInstruction("reti_or $t1 = $t2 , 100",
            	 "PC = r31; Bitwise OR immediate : Set $t1 to bitwise OR of $t2 and 12-bit immediate",
                BasicInstructionFormat.I_FORMAT,
                "000111 fffff 0101 sssss tttttttttttt",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                 
                     RegisterFile.updateRegister(operands[0],
                        RegisterFile.getValue(operands[1])
                        | (operands[2]));
					processJump(31);
                  }
               }));
         instructionList.add(
                 new BasicInstruction("reti_nor $t1 = $t2 , 100",
             	 "PC = r31; Bitwise NOR : Set $t1 to bitwise NOR of $t2 and 12-bit immed",
             	BasicInstructionFormat.I_FORMAT,
                "000111 fffff 0111 sssss tttttttttttt",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      RegisterFile.updateRegister(operands[0],
                         ~(RegisterFile.getValue(operands[1])
                         | RegisterFile.getValue(operands[2])));
					processJump(31);
                   }
                }));


         instructionList.add(
                new BasicInstruction("reti_xor $t1 = $t2 , 100",
            	 "PC = r31; Bitwise XOR immediate : Set $t1 to bitwise XOR of $t2 and  12-bit immediate",
                BasicInstructionFormat.I_FORMAT,
                "000111 fffff 0110 sssss tttttttttttt",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                  
                     RegisterFile.updateRegister(operands[0],
                        RegisterFile.getValue(operands[1])
                        ^ (operands[2]));
					processJump(31);
                  }
               }));					

         instructionList.add(
                 new BasicInstruction("reti_ror $t1 = $t2 , 100",
             	 "PC = r31; rotate right variable : Set $t1 to result of circular rotating $t2 right by number of bits specified by 12-bit immed",
                 BasicInstructionFormat.R_FORMAT,
                 "000111 fffff 1011 sssss tttttttttttt",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                   
                      
                      RegisterFile.updateRegister(operands[0],
                    		  Integer.rotateRight(RegisterFile.getValue(operands[1]), (RegisterFile.getValue(operands[2]))));
					  processJump(31);
                   }
                }));
         
         instructionList.add(
                new BasicInstruction("reti_sar $t1 = $t2 , 10",
                "PC = r31; Shift right arithmetic : Set $t1 to result of sign-extended shifting $t2 right by number of bits specified by immediate",
            	 BasicInstructionFormat.R_FORMAT,
                "000111 fffff 1010 sssss tttttttttttt",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                  // must sign-fill, so use ">>".
                     RegisterFile.updateRegister(operands[0],
                        RegisterFile.getValue(operands[1]) >> operands[2]);
					processJump(31);
                  }
               }));
         instructionList.add(
                 new BasicInstruction("reti_shr $t1 = $t2 , 100",
             	 "PC = r31; Shift right logical variable : Set $t1 to result of shifting $t2 right by number of bits specified by value in low-order 5 bits of $t3",
                 BasicInstructionFormat.R_FORMAT,
                 "000111 fffff 1001 sssss tttttttttttt",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                    
                      RegisterFile.updateRegister(operands[0],
                         RegisterFile.getValue(operands[1]) >>> 
                         (RegisterFile.getValue(operands[2])));
						processJump(31);
                   }
                }));
         instructionList.add(
                new BasicInstruction("reti_shl $t1 = $t2,10",
            	 "PC = r31; Shift left logical : Set $t1 to result of shifting $t2 left by number of bits specified by immediate",
                BasicInstructionFormat.R_FORMAT,
                "000111 fffff 1000 sssss tttttttttttt",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     RegisterFile.updateRegister(operands[0],
                        RegisterFile.getValue(operands[1]) << operands[2]);
					processJump(31);
                  }
               }));

       //set

         instructionList.add(
                 new BasicInstruction("set0 $t1 = 100",
                 "Rd = zero_extend(Imm21)",
             	 BasicInstructionFormat.R1_FORMAT,
                 "110000 fffff sssssssssssssssssssss",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      RegisterFile.updateRegister(operands[0], operands[1]);
                   }
                }));


          instructionList.add(
                 new BasicInstruction("set1 $t1 = -100",
                 "Rd = one_extend(Imm21)",
             	 BasicInstructionFormat.R1_FORMAT,
                 "110001 fffff sssssssssssssssssssss",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      RegisterFile.updateRegister(operands[0], operands[1] << 11 >>11);
                   }
                }));

          instructionList.add(
                 new BasicInstruction("sset $t1 = -100",
                 "Rd = Rd<<21 | zero_ext(Imm21)",
             	 BasicInstructionFormat.R1_FORMAT,
                 "110010 fffff sssssssssssssssssssss",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
 					 int rd = (RegisterFile.getValue(operands[0])<<21);
                      RegisterFile.updateRegister(operands[0], rd | (operands[1]));
                   }
                }));


          instructionList.add(
                 new BasicInstruction("addr $t1 = target",
                 "Rd = PC + sign_ext(Imm21<<2)",
             	 BasicInstructionFormat.R1_FORMAT,
                 "110011 fffff sssssssssssssssssssss",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      RegisterFile.updateRegister(operands[0],(RegisterFile.getProgramCounter()+ ((operands[1] << 11 >>11 )<<2)));
                   }
                }));

			   
//storex instructions

         instructionList.add(
                new BasicInstruction("sb $t1, $t2, 1 = $t3",
                "MEM[Ra + Rb<<s] <== 1 Rd",
            	 BasicInstructionFormat.R3_FORMAT,
                "001001 aaaaa fffff 0000 sssss 00000 tt",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     try
                     {
                        Globals.memory.setByte(
                            RegisterFile.getValue(operands[0])
                                    + (RegisterFile.getValue(operands[1])<<operands[2]),
                                    RegisterFile.getValue(operands[3])
                                            & 0x000000ff);
                     } 
                         catch (AddressErrorException e)
                        {
                           throw new ProcessingException(statement, e);
                        }
                  }
               }));

         instructionList.add(
                new BasicInstruction("sh $t1, $t2, 1 = $t3",
                "MEM[Ra + Rb<<s] <== 2 Rd",
            	 BasicInstructionFormat.R2_FORMAT,
                "001001 aaaaa fffff 0001 sssss 00000 tt",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     try
                     {
                        Globals.memory.setHalf(
                            RegisterFile.getValue(operands[0])
                                    + (RegisterFile.getValue(operands[1])<<operands[2]),
                                    RegisterFile.getValue(operands[3])
                                            & 0x0000ffff);
                     } 
                         catch (AddressErrorException e)
                        {
                           throw new ProcessingException(statement, e);
                        }
                  }
               }));				

         instructionList.add(
                new BasicInstruction("sw $t1, $t2, 1 = $t3 ",
                "MEM[Ra + Rb<<s] <== 4 Rd",
            	 BasicInstructionFormat.I_FORMAT,
                "001001 aaaaa fffff 0010 sssss 00000 tt",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     try
                     {
                        Globals.memory.setWord(
                            RegisterFile.getValue(operands[0])
                                    + (RegisterFile.getValue(operands[1])<<operands[2]),
                                    RegisterFile.getValue(operands[3]));
                     } 
                         catch (AddressErrorException e)
                        {
                           throw new ProcessingException(statement, e);
                        }
                  }
               }));

//loadx

         instructionList.add(
                new BasicInstruction("lb $t1 = $t2 , $t3, 1",
                "Rd <== 1s MEM[Ra + Rb<<s]",
            	 BasicInstructionFormat.R3_FORMAT,
                "001010 fffff sssss 0000 ttttt 00000 aa",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     try
                     {
                        RegisterFile.updateRegister(operands[0],
                            Globals.memory.getByte(
                            RegisterFile.getValue(operands[1])
                                    +  ((RegisterFile.getValue(operands[2]))<<operands[3]))
                                            << 24
                                            >> 24);
                     } 
                         catch (AddressErrorException e)
                        {
                           throw new ProcessingException(statement, e);
                        }
                  }
               }));

         instructionList.add(
                new BasicInstruction("lh $t1 = $t2 , $t3 , 1",
                "Rd <== 2s MEM[Ra + Rb<<s]",
            	 BasicInstructionFormat.R3_FORMAT,
                "001010 fffff sssss 0001 ttttt 00000 aa",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     try
                     {
                        RegisterFile.updateRegister(operands[0],
                            Globals.memory.getHalf(
                           RegisterFile.getValue(operands[1])
                                    +  ((RegisterFile.getValue(operands[2]))<<operands[3]))
                                            << 16
                                            >> 16);
                     } 
                         catch (AddressErrorException e)
                        {
                           throw new ProcessingException(statement, e);
                        }
                  }
               }));

         instructionList.add(
                new BasicInstruction("lhu $t1 = $t2 , $t3 , 1",
                "Rd <== 2z MEM[Ra + Rb<<s]",
            	 BasicInstructionFormat.R3_FORMAT,
                "001010 fffff sssss 1001 ttttt 00000 aa",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     try
                     {
                        RegisterFile.updateRegister(operands[0],
                            Globals.memory.getHalf(
                            RegisterFile.getValue(operands[1])
                                    +  ((RegisterFile.getValue(operands[2]))<<operands[3]))
                                            & 0x0000ffff);
                     } 
                         catch (AddressErrorException e)
                        {
                           throw new ProcessingException(statement, e);
                        }
                  }
               }));

         instructionList.add(
                new BasicInstruction("lbu $t1 = $t2 , $t3 , 1",
                "Rd <== 1z MEM[Ra + Rb<<s]",
            	 BasicInstructionFormat.R3_FORMAT,
                "001010 fffff sssss 1000 ttttt 00000 aa",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     try
                     {
                        RegisterFile.updateRegister(operands[0],
                            Globals.memory.getByte(
                            RegisterFile.getValue(operands[1])
                                    +  ((RegisterFile.getValue(operands[2]))<<operands[3]))
                                            & 0x000000ff);
                     } 
                         catch (AddressErrorException e)
                        {
                           throw new ProcessingException(statement, e);
                        }
                  }
               }));

         instructionList.add(
                new BasicInstruction("lw $t1 = $t2 , $t3 , 1",
            	 "Rd <== 4s MEM[Ra + Rb<<s]",
                BasicInstructionFormat.R3_FORMAT,
                "001010 fffff sssss 0010 ttttt 00000 aa",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     try
                     {
                        RegisterFile.updateRegister(operands[0],
                            Globals.memory.getWord(
                            RegisterFile.getValue(operands[1])
                                    +  ((RegisterFile.getValue(operands[2]))<<operands[3])));
                     } 
                         catch (AddressErrorException e)
                        {
                           throw new ProcessingException(statement, e);
                        }
                  }
               }));

//store

         instructionList.add(
                new BasicInstruction("sb $t1 , -100 = $t2",
                "MEM[Ra+Imm12] <== 1 Rd",
            	 BasicInstructionFormat.R2_FORMAT,
                "001001 ttttt fffff 0000 ssssssssssss",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     try
                     {
                        Globals.memory.setByte(
                            RegisterFile.getValue(operands[0])
                                    + (operands[1] << 11 >> 11),
                                    RegisterFile.getValue(operands[2])
                                            & 0x000000ff);
                     } 
                         catch (AddressErrorException e)
                        {
                           throw new ProcessingException(statement, e);
                        }
                  }
               }));

         instructionList.add(
                new BasicInstruction("sh $t1 , -100 = $t2",
                "MEM[Ra+Imm12] <== 2 Rd",
            	 BasicInstructionFormat.R2_FORMAT,
                "001001 ttttt fffff 0001 ssssssssssss",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     try
                     {
                        Globals.memory.setHalf(
                            RegisterFile.getValue(operands[0])
                                    + (operands[1] << 11 >> 11),
                                    RegisterFile.getValue(operands[2])
                                            & 0x0000ffff);
                     } 
                         catch (AddressErrorException e)
                        {
                           throw new ProcessingException(statement, e);
                        }
                  }
               }));				

         instructionList.add(
                new BasicInstruction("sw $t1 , -100 = $t2",
                "MEM[Ra+Imm12] <== 4 Rd",
            	 BasicInstructionFormat.R2_FORMAT,
                "001001 ttttt fffff 0010 ssssssssssss",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     try
                     {
                        Globals.memory.setWord(
                            RegisterFile.getValue(operands[0])
                                    + (operands[1] << 11 >> 11),
                                    RegisterFile.getValue(operands[2]));
                     } 
                         catch (AddressErrorException e)
                        {
                           throw new ProcessingException(statement, e);
                        }
                  }
               }));

//load

         instructionList.add(
                new BasicInstruction("lb $t1 = $t2 -100",
                "Rd <== 1s MEM[Ra+Imm12]",
            	 BasicInstructionFormat.R2_FORMAT,
                "001000 fffff sssss 0000 tttttttttttt",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     try
                     {
                        RegisterFile.updateRegister(operands[0],
                            Globals.memory.getByte(
                            RegisterFile.getValue(operands[1])
                                    + (operands[2] << 11 >> 11))
                                            << 24
                                            >> 24);
                     } 
                         catch (AddressErrorException e)
                        {
                           throw new ProcessingException(statement, e);
                        }
                  }
               }));

         instructionList.add(
                new BasicInstruction("lh $t1 = $t2 -100",
                "Rd <== 2s MEM[Ra+Imm12]",
            	 BasicInstructionFormat.R2_FORMAT,
                "001000 fffff sssss 0001 tttttttttttt",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     try
                     {
                        RegisterFile.updateRegister(operands[0],
                            Globals.memory.getHalf(
                            RegisterFile.getValue(operands[1])
                                    + (operands[2] << 11 >> 11))
                                            << 16
                                            >> 16);
                     } 
                         catch (AddressErrorException e)
                        {
                           throw new ProcessingException(statement, e);
                        }
                  }
               }));

         instructionList.add(
                new BasicInstruction("lhu $t1 = $t2 -100",
                "Rd <== 4s MEM[Ra+Imm12]",
            	 BasicInstructionFormat.R2_FORMAT,
                "001000 fffff sssss 1001 tttttttttttt",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     try
                     {
                     // offset is sign-extended and loaded halfword value is zero-extended
                        RegisterFile.updateRegister(operands[0],
                            Globals.memory.getHalf(
                            RegisterFile.getValue(operands[1])
                                    + (operands[2] << 11 >> 11))
                                            & 0x0000ffff);
                     } 
                         catch (AddressErrorException e)
                        {
                           throw new ProcessingException(statement, e);
                        }
                  }
               }));

         instructionList.add(
                new BasicInstruction("lbu $t1 = $t2 -100",
                "Rd <== 1z MEM[Ra+Imm12]",
            	 BasicInstructionFormat.R2_FORMAT,
                "001000 fffff sssss 1000 tttttttttttt",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     try
                     {
                        RegisterFile.updateRegister(operands[0],
                            Globals.memory.getByte(
                            RegisterFile.getValue(operands[1])
                                    + (operands[2] << 11 >> 11))
                                            & 0x000000ff);
                     } 
                         catch (AddressErrorException e)
                        {
                           throw new ProcessingException(statement, e);
                        }
                  }
               }));

         instructionList.add(
                new BasicInstruction("lw $t1 = $t2 -100",
            	 "Rd <== 4s MEM[Ra+Imm12]",
                BasicInstructionFormat.R2_FORMAT,
                "001000 fffff sssss 0010 tttttttttttt",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     try
                     {
                        RegisterFile.updateRegister(operands[0],
                            Globals.memory.getWord(
                            RegisterFile.getValue(operands[1])
                                    + (operands[2] << 11 >> 11)));
                     } 
                         catch (AddressErrorException e)
                        {
                           throw new ProcessingException(statement, e);
                        }
                  }
               }));

//branch

         instructionList.add(
                new BasicInstruction("beqz $t1,label",
                "Branch if equal to zero : Branch to statement at label's address if $t1 is equal to zero",
            	 BasicInstructionFormat.R1_FORMAT,
                "101000 fffff sssssssssssssssssssss",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                  
                     if (RegisterFile.getValue(operands[0])
                        == 0)
                     {
                        processBranch(operands[1]);
                     }
                  }
               }));

         instructionList.add(
                new BasicInstruction("bnez $t1,label",
                "Branch if not equal : Branch to statement at label's address if $t1 is not equal to zero",
            	 BasicInstructionFormat.R1_FORMAT,
                "101001 fffff sssssssssssssssssssss",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                  
                     if (RegisterFile.getValue(operands[0])
                        != 0)
                     {
                        processBranch(operands[1]);
                     }
                  }
               }));

         instructionList.add(
                new BasicInstruction("bltz $t1,label",
                "Branch if less than zero : Branch to statement at label's address if $t1 and $t2 are equal",
            	 BasicInstructionFormat.R1_FORMAT,
                "101010 fffff sssssssssssssssssssss",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                  
                     if (RegisterFile.getValue(operands[0])
                        < 0)
                     {
                        processBranch(operands[1]);
                     }
                  }
               }));

         instructionList.add(
                new BasicInstruction("bgez $t1,label",
                "Branch if equal to zero : Branch to statement at label's address if $t1 is greater than or equal to 0",
            	 BasicInstructionFormat.R1_FORMAT,
                "101011 fffff sssssssssssssssssssss",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                  
                     if (RegisterFile.getValue(operands[0])
                        >= 0)
                     {
                        processBranch(operands[1]);
                     }
                  }
               }));

         instructionList.add(
                new BasicInstruction("bgtz $t1 , label",
                "Branch if greater than zero : Branch to statement at label's address if $t1 is greater than zero",
            	 BasicInstructionFormat.R1_FORMAT,
                "101100 fffff sssssssssssssssssssss",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                  
                     if (RegisterFile.getValue(operands[0])
                        > 0);
                     {
                        processBranch(operands[1]);
                     }
                  }
               }));

         instructionList.add(
                new BasicInstruction("blez $t1 , label",
                "Branch if less than or equal to zero : Branch to statement at label's address if $t1 is less than or equal to zero",
            	 BasicInstructionFormat.R1_FORMAT,
                "101101 fffff sssssssssssssssssssss",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                  
                     if (RegisterFile.getValue(operands[0])
                        <= 0)
                     {
                        processBranch(operands[1]);
                     }
                  }
               }));

         instructionList.add(
                 new BasicInstruction("jr $t1", 
             	 "Jump register unconditionally : Jump to statement whose address is in $t1",
             	 BasicInstructionFormat.R2_FORMAT,
                 "011110 00000 fffff 00000 00000 000000",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      processJump(RegisterFile.getValue(operands[0])&(-4));
                   }
                }));

          instructionList.add(
                 new BasicInstruction("jal target",
                 "Jump and link : Set $ra to Program Counter (return address) then jump to statement at target address",
             	 BasicInstructionFormat.R0_FORMAT,
                 "111001 ffffffffffffffffffffffffff",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      processReturnAddress(31);// RegisterFile.updateRegister(31, RegisterFile.getProgramCounter());
                      processJump(
                         (RegisterFile.getProgramCounter() & 0xF0000000)
                                 | (operands[0]));
                   }
                }));

          instructionList.add(
                 new BasicInstruction("jalr $t1 , $t2",
                 "Jump and link register : Set $t1 to Program Counter (return address) then jump to statement whose address is in $t2",
             	 BasicInstructionFormat.R2_FORMAT,
                 "011111 fffff sssss 00000 00000 000000",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      processReturnAddress(operands[0]);//RegisterFile.updateRegister(operands[0], RegisterFile.getProgramCounter());
                      processJump(RegisterFile.getValue(operands[1])&(-4));
                   }
                }));


          instructionList.add(
                 new BasicInstruction("j target", 
             	 "Jump unconditionally : Jump to statement at target address",
             	 BasicInstructionFormat.R0_FORMAT,
                 "111000 ffffffffffffffffffffffffff",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      int[] operands = statement.getOperands();
                      processJump(
                         ((RegisterFile.getProgramCounter() & 0xF0000000)
                                 | (operands[0] << 2)));            
                   }
                }));
         
         instructionList.add(
                 new BasicInstruction("break", 
             	 "Break execution : Terminate program execution with exception",
             	 BasicInstructionFormat.R0_FORMAT,
                 "000000 00000 00000 00000 00000 001101",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {  // At this time I don't have exception processing or trap handlers
                      // so will just halt execution with a message.
                      throw new ProcessingException(statement, "break instruction executed; no code given.",
                         Exceptions.BREAKPOINT_EXCEPTION);
                   }
                }));				

//syscall
         instructionList.add(
                 new BasicInstruction("syscall", 
             	 "Issue a system call : Execute the system call specified by value in $v0",
             	 BasicInstructionFormat.R0_FORMAT,
                 "000000 00000 00000 00000 00000 001100",
                 new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                   {
                      findAndSimulateSyscall(RegisterFile.getValue(2),statement);
                   }
                }));
      			
        ////////////// READ PSEUDO-INSTRUCTION SPECS FROM DATA FILE AND ADD //////////////////////
         addPseudoInstructions();
      	
        ////////////// GET AND CREATE LIST OF SYSCALL FUNCTION OBJECTS ////////////////////
         syscallLoader = new SyscallLoader();
         syscallLoader.loadSyscalls();
      	
        // Initialization step.  Create token list for each instruction example.  This is
        // used by parser to determine user program correct syntax.
         for (int i = 0; i < instructionList.size(); i++)
         {
            Instruction inst = (Instruction) instructionList.get(i);
            inst.createExampleTokenList();
         }

		 HashMap maskMap = new HashMap();
		 ArrayList matchMaps = new ArrayList();
		 for (int i = 0; i < instructionList.size(); i++) {
		 	Object rawInstr = instructionList.get(i);
			if (rawInstr instanceof BasicInstruction) {
				BasicInstruction basic = (BasicInstruction) rawInstr;
				Integer mask = Integer.valueOf(basic.getOpcodeMask());
				Integer match = Integer.valueOf(basic.getOpcodeMatch());
				HashMap matchMap = (HashMap) maskMap.get(mask);
				if (matchMap == null) {
					matchMap = new HashMap();
					maskMap.put(mask, matchMap);
					matchMaps.add(new MatchMap(mask, matchMap));
				}
				matchMap.put(match, basic);
			}
		 }
		 Collections.sort(matchMaps);
		 this.opcodeMatchMaps = matchMaps;
      }

	public BasicInstruction findByBinaryCode(int binaryInstr) {
		ArrayList matchMaps = this.opcodeMatchMaps;
		for (int i = 0; i < matchMaps.size(); i++) {
			MatchMap map = (MatchMap) matchMaps.get(i);
			BasicInstruction ret = map.find(binaryInstr);
			if (ret != null) return ret;
		}
		return null;
	}
   	
    /*  METHOD TO ADD PSEUDO-INSTRUCTIONS
    */
   
       private void addPseudoInstructions()
      {
         InputStream is = null;
         BufferedReader in = null;
         try
         {
            // leading "/" prevents package name being prepended to filepath.
            is = this.getClass().getResourceAsStream("/PseudoOps.txt");
            in = new BufferedReader(new InputStreamReader(is));
         } 
             catch (NullPointerException e)
            {
               System.out.println(
                    "Error: MIPS pseudo-instruction file PseudoOps.txt not found.");
               System.exit(0);
            }
         try
         {
            String line, pseudoOp, template, firstTemplate, token;
            String description;
            StringTokenizer tokenizer;
            while ((line = in.readLine()) != null) {
                // skip over: comment lines, empty lines, lines starting with blank.
               if (!line.startsWith("#") && !line.startsWith(" ")
                        && line.length() > 0)  {  
                  description = "";
                  tokenizer = new StringTokenizer(line, "\t");
                  pseudoOp = tokenizer.nextToken();
                  template = "";
                  firstTemplate = null;
                  while (tokenizer.hasMoreTokens()) {
                     token = tokenizer.nextToken();
                     if (token.startsWith("#")) {  
                        // Optional description must be last token in the line.
                        description = token.substring(1);
                        break;
                     }
                     if (token.startsWith("COMPACT")) {
                        // has second template for Compact (16-bit) memory config -- added DPS 3 Aug 2009
                        firstTemplate = template;
                        template = "";
                        continue;
                     } 
                     template = template + token;
                     if (tokenizer.hasMoreTokens()) {
                        template = template + "\n";
                     }
                  }
                  ExtendedInstruction inst = (firstTemplate == null)
                         ? new ExtendedInstruction(pseudoOp, template, description)
                     	 : new ExtendedInstruction(pseudoOp, firstTemplate, template, description);
                  instructionList.add(inst);
               	//if (firstTemplate != null) System.out.println("\npseudoOp: "+pseudoOp+"\ndefault template:\n"+firstTemplate+"\ncompact template:\n"+template);
               }
            }
            in.close();
         } 
             catch (IOException ioe)
            {
               System.out.println(
                    "Internal Error: MIPS pseudo-instructions could not be loaded.");
               System.exit(0);
            } 
             catch (Exception ioe)
            {
               System.out.println(
                    "Error: Invalid MIPS pseudo-instruction specification.");
               System.exit(0);
            }
      
      }
   	
    /**
     *  Given an operator mnemonic, will return the corresponding Instruction object(s)
     *  from the instruction set.  Uses straight linear search technique.
     *  @param name operator mnemonic (e.g. addi, sw,...)
     *  @return list of corresponding Instruction object(s), or null if not found.
     */
       public ArrayList matchOperator(String name)
      {
         ArrayList matchingInstructions = null;
        // Linear search for now....
         for (int i = 0; i < instructionList.size(); i++)
         {
            if (((Instruction) instructionList.get(i)).getName().equalsIgnoreCase(name))
            {
               if (matchingInstructions == null) 
                  matchingInstructions = new ArrayList();
               matchingInstructions.add(instructionList.get(i));
            }
         }
         return matchingInstructions;
      }
   
   
    /**
     *  Given a string, will return the Instruction object(s) from the instruction
     *  set whose operator mnemonic prefix matches it.  Case-insensitive.  For example
     *  "s" will match "sw", "sh", "sb", etc.  Uses straight linear search technique.
     *  @param name a string
     *  @return list of matching Instruction object(s), or null if none match.
     */
       public ArrayList prefixMatchOperator(String name)
      {
         ArrayList matchingInstructions = null;
        // Linear search for now....
         if (name != null) {
            for (int i = 0; i < instructionList.size(); i++)
            {
               if (((Instruction) instructionList.get(i)).getName().toLowerCase().startsWith(name.toLowerCase()))
               {
                  if (matchingInstructions == null) 
                     matchingInstructions = new ArrayList();
                  matchingInstructions.add(instructionList.get(i));
               }
            }
         }
         return matchingInstructions;
      }
   	
   	/*
   	 * Method to find and invoke a syscall given its service number.  Each syscall
   	 * function is represented by an object in an array list.  Each object is of
   	 * a class that implements Syscall or extends AbstractSyscall.
   	 */
   	 
       private void findAndSimulateSyscall(int number, ProgramStatement statement) 
                                                        throws ProcessingException {
         Syscall service = syscallLoader.findSyscall(number);
         if (service != null) {
            service.simulate(statement);
            return;
         }
         throw new ProcessingException(statement,
              "invalid or unimplemented syscall service: " +
              number + " ", Exceptions.SYSCALL_EXCEPTION);
      }
   	
   	/*
   	 * Method to process a successful branch condition.  DO NOT USE WITH JUMP
   	 * INSTRUCTIONS!  The branch operand is a relative displacement in words
   	 * whereas the jump operand is an absolute address in bytes.
   	 *
   	 * The parameter is displacement operand from instruction.
   	 *
   	 * Handles delayed branching if that setting is enabled.
   	 */
   	 // 4 January 2008 DPS:  The subtraction of 4 bytes (instruction length) after
   	 // the shift has been removed.  It is left in as commented-out code below.
   	 // This has the effect of always branching as if delayed branching is enabled, 
   	 // even if it isn't.  This mod must work in conjunction with
   	 // ProgramStatement.java, buildBasicStatementFromBasicInstruction() method near
   	 // the bottom (currently line 194, heavily commented).
   	 
       private void processBranch(int displacement) {
         if (Globals.getSettings().getDelayedBranchingEnabled()) {
            // Register the branch target address (absolute byte address).
            DelayedBranch.register(RegisterFile.getProgramCounter() + (displacement << 2));
         } 
         else {
            // Decrement needed because PC has already been incremented
            RegisterFile.setProgramCounter(
                RegisterFile.getProgramCounter()
                  + (displacement << 2)); // - Instruction.INSTRUCTION_LENGTH);	
         }	 
      }
   
   	/*
   	 * Method to process a jump.  DO NOT USE WITH BRANCH INSTRUCTIONS!  
   	 * The branch operand is a relative displacement in words
   	 * whereas the jump operand is an absolute address in bytes.
   	 *
   	 * The parameter is jump target absolute byte address.
   	 *
   	 * Handles delayed branching if that setting is enabled.
   	 */
   	 
       private void processJump(int targetAddress) {
         if (Globals.getSettings().getDelayedBranchingEnabled()) {
            DelayedBranch.register(targetAddress);
         } 
         else {
            RegisterFile.setProgramCounter(targetAddress);
         }	 
      }
   
   	/*
   	 * Method to process storing of a return address in the given
   	 * register.  This is used only by the "and link"
   	 * instructions: jal, jalr, bltzal, bgezal.  If delayed branching
   	 * setting is off, the return address is the address of the
   	 * next instruction (e.g. the current PC value).  If on, the
   	 * return address is the instruction following that, to skip over
   	 * the delay slot.
   	 *
   	 * The parameter is register number to receive the return address.
   	 */
   	 
       private void processReturnAddress(int register) {
         RegisterFile.updateRegister(register, RegisterFile.getProgramCounter() +
                 ((Globals.getSettings().getDelayedBranchingEnabled()) ? 
            	  Instruction.INSTRUCTION_LENGTH : 0) );	 
      }

	  private static class MatchMap implements Comparable {
	  	private int mask;
		private int maskLength; // number of 1 bits in mask
		private HashMap matchMap;

		public MatchMap(int mask, HashMap matchMap) {
			this.mask = mask;
			this.matchMap = matchMap;

			int k = 0;
			int n = mask;
			while (n != 0) {
				k++;
				n &= n - 1;
			}
			this.maskLength = k;
		}

		public boolean equals(Object o) {
			return o instanceof MatchMap && mask == ((MatchMap) o).mask;
		}

		public int compareTo(Object other) {
			MatchMap o = (MatchMap) other;
			int d = o.maskLength - this.maskLength;
			if (d == 0) d = this.mask - o.mask;
			return d;
		}

		public BasicInstruction find(int instr) {
			int match = Integer.valueOf(instr & mask);
			return (BasicInstruction) matchMap.get(match);
		}
	}
   }

