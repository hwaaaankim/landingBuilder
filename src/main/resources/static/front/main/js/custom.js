(function ($) {
    'use strict';
	
	// 01. Initialize FullPage
	
	$('#page-wrapper').fullpage({
		anchors: ['homescreen', 'whoweare', 'whatwedo', 'project', 'reviews', 'team', 'contactus'],
		sectionsColor: ['#9BB5BC', '#000000', '#7E8F7C', '#3F9F65', '#d19e3b', '#38BBCB','#4d8599'],
		menu: "#top-menu",
		navigation: true,
		navigationPosition: 'right',
		navigationTooltips: ['Splashr', 'Who We Are', 'What We Do', 'Our Project', 'Reviews', 'Our Team', 'Contact Us']
	});
	
	// 02. Activate Slider for Project and Team  
	
	$('.project-slick, .team-slick').owlCarousel({
    	loop: false,
    	margin: 10,
		navText: ['<i class="fa fa-arrow-left">','<i class="fa fa-arrow-right">'],
    	responsiveClass: true,
    	responsive: {
			0:{
				items:1,
				nav:true
			},
			600:{
				items:2,
				nav:true
			},
			1000:{
				items:3,
				nav:true,
				loop:false
			}
    	}
	});
	
	// 03. Initiate Feedback
	
	var $owl = $('.feedbackslider');
		$owl.owlCarousel({
		items:1,
		loop: false,
		nav: true,
		navText: ['<i class="fa fa-arrow-left">','<i class="fa fa-arrow-right">'],
		autoplay: true,
		autoplayHoverPause: true
	});

	// 04. Preloader 
	
	$(window).on('load', function () {
		$(".square-block").fadeOut();
		$('#preload-block').fadeOut('slow', function () {
			$(this).remove();
		});
	});

})(jQuery);