InFIX for Java Sample Application for use on Unix flavored OS.
All the files needed to run InFIX are located in this directory.
InFIX is 100% pure Java so it's possible to port the runExample.sh to Windows
with a little tweaking.
The example app is scripted to run on Unix flavored Systems.

Contents include:
	#1 Antlr 4 libraries (http://antlr.org)
	#2 Simple Logging Facade (http://slf4j.org)
	#3 Infix library (infix-x.x.x.jar)
	#4 Sample Application.

See runExample.sh to see how to build the classpath and run the sample
application.

Visit the infix help website for usage and help with actions: 
http://infix.globalforge.com/roadmap.html

Usage:
-----
./runExample.sh
The example app will display a static FIX message and wait for user input
at the command prompt.
Apply an action to the FIX message and hit enter.

Example: &207="UK" <return>

The example app will display a transformed FIX message after applying the action
and then re-display the original message and wait for input again.

Code Usage:
----------
You only need to know 3 lines of code to transform a FIX message
in your application:

// Define some actions
1. String actionsStr = "&116=&115 ; &115=&49";
2. InfixActions actions = new InfixActions(actionsStr);
// Apply the actions to a FIX message and obtain the result
3. String outputFIXMessage = actions.transformFIXMsg(inputFIXMessage); 


Michael Starkie
globalforge@gmail.com

http://infix.globalforge.com/

© Copyright 2019-2020 Global Forge ®, LLC. All Rights Reserved.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
