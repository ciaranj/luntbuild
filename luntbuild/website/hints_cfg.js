var HINTS_CFG = {
	'top'        : 5, // a vertical offset of a hint from mouse pointer
	'left'       : 5, // a horizontal offset of a hint from mouse pointer
	'css'        : 'hintsClass', // a style class name for all hints, TD object
	'show_delay' : 500, // a delay between object mouseover and hint appearing
	'hide_delay' : 2000, // a delay between hint appearing and hint hiding
	'wise'       : true,
	'follow'     : true,
	'z-index'    : 100 // a z-index for all hint layers
},

HINTS_ITEMS = {
	0: 'Username: demo<br>Password: demo',
	1: '<strong>Weekly builds might not be data<br>compatible with previous/future versions.</strong>'
};

var myHint = new THints (HINTS_CFG, HINTS_ITEMS);

function wrap (s_, b_ques) {
	return "<table cellpadding='0' cellspacing='0' border='0' style='-moz-opacity:90%;filter:progid:DXImageTransform.Microsoft.dropShadow(Color=#777777,offX=4,offY=4)'><tr><td rowspan='2'><img src='img/1"+(b_ques?"q":"")+".gif'></td><td><img src='/img/pixel.gif' width='1' height='15'></td></tr><tr><td background='img/2.gif' height='28' nowrap>"+s_+"</td><td><img src='img/4.gif'></td></tr></table>"
}

function wrap_img (s_file, s_title) {
	return "<table cellpadding=5 bgcolor=white style='border:1px solid #777777'><tr><td><img src='img/k0"+s_file+".jpg' class='picI'></td></tr><tr><td align=center>"+s_title+"</td></tr></table>"
}
