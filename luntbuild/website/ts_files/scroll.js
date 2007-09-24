// Title: Tigra Scroller
// Description: See the demo at url
// URL: http://www.softcomplex.com/products/tigra_scroller/
// Version: 1.5
// Date: 07-03-2003 (mm-dd-yyyy)
// Note: Permission given to use this script in ANY kind of applications if
//    header lines are left unchanged.

// set correct path to Tigra Scroller files
var Tscroll_path_to_files = 'ts_files/'

// please, don't change anything below this line
function Tscroll_init (id) {
	document.write ('<iframe id="Tscr' + id + '" scrolling=no frameborder=no src="' + Tscroll_path_to_files + 'scroll.html?' + id + '" width="1" height="1"></iframe>');
}