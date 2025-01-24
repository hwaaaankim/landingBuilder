var app = app || {};

app.init = function () {
  //shop12.makeshop.jp/makeshop/newmanager/flex_design_page_editor.html?design_set_id=2&page_type=1#
  https: app.showMenu();
  app.anchorLink();
  app.counterNumber();
  app.header();
  app.mainvisualSlider();
  app.sliderApp();
  app.sliderPremiumPlan();
  app.Accordion();
  app.sceneSlider();
  app.gallerySlider();
  app.detectBrower();
  app.modal();
  app.scrollOnPC();
  app.getHeightWindow();
};
app.detectBrower = function () {
  var BrowserDetect = {
    init: function () {
      this.browser = this.searchString(this.dataBrowser) || "Other";
      this.version =
        this.searchVersion(navigator.userAgent) ||
        this.searchVersion(navigator.appVersion) ||
        "Unknown";
    },
    searchString: function (data) {
      for (var i = 0; i < data.length; i++) {
        var dataString = data[i].string;
        this.versionSearchString = data[i].subString;

        if (dataString.indexOf(data[i].subString) !== -1) {
          return data[i].identity;
        }
      }
    },
    searchVersion: function (dataString) {
      var index = dataString.indexOf(this.versionSearchString);
      if (index === -1) {
        return;
      }

      var rv = dataString.indexOf("rv:");
      if (this.versionSearchString === "Trident" && rv !== -1) {
        return parseFloat(dataString.substring(rv + 3));
      } else {
        return parseFloat(
          dataString.substring(index + this.versionSearchString.length + 1)
        );
      }
    },

    dataBrowser: [
      { string: navigator.userAgent, subString: "Edge", identity: "MS Edge" },
      { string: navigator.userAgent, subString: "MSIE", identity: "Explorer" },
      {
        string: navigator.userAgent,
        subString: "Trident",
        identity: "Explorer",
      },
      {
        string: navigator.userAgent,
        subString: "Firefox",
        identity: "Firefox",
      },
      { string: navigator.userAgent, subString: "Opera", identity: "Opera" },
      { string: navigator.userAgent, subString: "OPR", identity: "Opera" },

      { string: navigator.userAgent, subString: "Chrome", identity: "Chrome" },
      { string: navigator.userAgent, subString: "Safari", identity: "Safari" },
    ],
  };

  BrowserDetect.init();

  var bv = BrowserDetect.browser;
  if (bv == "Chrome") {
    $("body").addClass("chrome");
  } else if (bv == "MS Edge") {
    $("body").addClass("edge");
  } else if (bv == "Explorer") {
    $("body").addClass("ie");
  } else if (bv == "Firefox") {
    $("body").addClass("Firefox");
  } else if (bv == "Safari") {
    $("body").addClass("safari");
  }
};
app.mainvisualSlider = function () {
  const swiper = new Swiper(".js-mainvisual-slider", {
    slidesPerView: 1,
    spaceBetween: 0,
    loop: true,
    centeredSlides: true,
    slideActiveClass: "active",

    autoplay: {
      enabled: true,
      delay: 4000,
    },

    //20241018
    pagination: {
      el: ".swiper-pagination",
      clickable: true,
      type: "bullets",
    },
  });
};

app.sceneSlider = function () {
  const swiper = new Swiper(".js-scene-slider", {
    slidesPerView: 1,
    spaceBetween: 11,
    loop: true,
    centeredSlides: true,
    navigation: {
      nextEl: ".scene-button-next",
      prevEl: ".scene-button-prev",
    },
  });
};

app.counterNumber = function () {
  let btnCart = $(".btn-addToCart");
  $(".js-counter .minus").click(function () {
    var $input = $(this).parent().find("input");
    var count = parseInt($input.val()) - 1;
    count = count < 0 ? 0 : count;
    $input.val(count);
    $input.change();
    return false;
  });
  $(".js-counter .plus").click(function () {
    var $input = $(this).parent().find("input");
    $input.val(parseInt($input.val()) + 1);
    $input.change();
    return false;
  });

  $(".js-counter").on("click", function () {
    var $input = $(this).find("input");
    $input.val(parseInt($input.val()) + 1);
    $input.change();
    btnCart.parent().fadeIn(100);
  });

  $("[data-sku]").on("change", function () {
    updateURL();
  });

  function updateURL() {
    let cartURL =
      "https://www.kokuyo-shop.jp/sc/_AddMultiProduct.aspx?goCart=1";
    let skuList = Array.from($("[data-sku]")).filter(function (sku, i) {
      return $(sku).val() > 0;
    });

    skuList.forEach((element, i) => {
      let sku = $(element).data("sku");
      let qty = $(element).val();
      cartURL += "&sku_" + i + "=" + sku + "&qty_" + i + "=" + qty;
    });

    btnCart.attr("href", cartURL + "&sitediv=hf");
  }

  $("[data-combo]").on("change", function () {
    updateURLCombo();
  });

  function updateURLCombo() {
    let cartURL =
      "https://www.kokuyo-shop.jp/sc/_AddMultiProduct.aspx?goCart=1";
    let skuList = Array.from($("[data-sku]")).filter(function (sku, i) {
      return $(sku).val() > 0;
    });

    skuList.forEach((element, i) => {
      let sku = $(element).data("sku");
      let qty = $(element).val();
      cartURL += "&sku_" + i + "=" + sku + "&qty_" + i + "=" + qty;
    });

    btnCart.attr("href", cartURL + "&sitediv=hf");
  }
};
/*
app.showMenu = function () {
	let ele = $(".js-menu");
	if (ele.length) {
		ele.on('click', function (e) {
			$(this).toggleClass('is-active');
			let vh = window.innerHeight * 0.01;
			document.documentElement.style.setProperty('--vh', `${vh}px`);
		});
	}
}*/

app.showMenu = function () {
  let ele = $(".js-menu");
  let globalnavi = $(".global-navi");

  $(ele).on("click", function (e) {
    e.preventDefault();
    if ($(ele).hasClass("is-active")) {
      app.resumeScroll();
      $(ele).removeClass("is-active");
      globalnavi.stop().slideUp(300);
    } else {
      app.stopScroll();
      $(ele).addClass("is-active");
      globalnavi.stop().slideDown(300);
    }
  });
};

app.getHeightWindow = function () {
  let vh = window.innerHeight * 0.01;
  document.documentElement.style.setProperty("--vh", `${vh}px`);
};

app.tab = function () {
  $(document).on("click", ".tab a", function (e) {
    e.preventDefault();
    let target = $(this).attr("href").split("#")[1];

    $(this).parent().addClass("active").siblings().removeClass("active");
    $('[data-id="' + target + '"]')
      .fadeIn(0)
      .siblings()
      .fadeOut(0);
    history.pushState({}, "", "#" + target);
  });

  if (location.hash && $(".tab li a[href='" + location.hash + "']").length) {
    $(".tab li a[href='" + location.hash + "']").trigger("click");

    $(".pagination a.page-numbers").each(function (i, a) {
      $(a).attr(
        "href",
        $(a).attr("href") + "#" + $(a).parents(".tab-box").attr("data-id")
      );
    });
  } else {
    $(".tab li:first-child a").trigger("click");
    history.replaceState(null, null, " ");
  }
};

app.anchorLink = function () {
  $(".anchor-link").click(function () {
    if (
      location.pathname.replace(/^\//, "") ==
        this.pathname.replace(/^\//, "") &&
      location.hostname == this.hostname
    ) {
      var target = $(this.hash);
      target = target.length ? target : $("[name=" + this.hash.slice(1) + "]");
      if (target.length) {
        $("html,body").animate(
          {
            scrollTop: target.offset().top,
          },
          1000
        );
        return false;
      }
    }
  });
};

app.stopScroll = function () {
  scrollTop = $(window).scrollTop();
  scrollLeft = $(window).scrollLeft();
  $("html")
    .addClass("noscroll")
    .css("top", -scrollTop + "px");
  $("body").addClass("hide-chatbox");
};

app.resumeScroll = function () {
  $("html").removeClass("noscroll");
  $("body").removeClass("hide-chatbox");
  $(window).scrollTop(scrollTop);
  $(window).scrollLeft(scrollLeft);
};

app.header = function () {
  $(window).on("scroll", function () {
    let top = $(this).scrollTop();

    if (top > 200) {
      $(".p-header").addClass("show");
    } else {
      $(".p-header").removeClass("show");
    }
  });

  // if( $('.p-story').length ) {
  $(window).on("scroll", function () {
    let top = $(this).scrollTop();

    if (top + $(window).height() > 1000) {
      $(".p-other-nav").addClass("show");
    } else {
      $(".p-other-nav").removeClass("show");
    }
  });
  // }
};

app.sliderApp = function () {
  $(".js-package-slide").slick({
    slidesToShow: 1,
    slidesToScroll: 1,
    infinite: false,
    dots: false,
    arrows: false,
    autoplay: false,
    //autoplaySpeed: 5000,
    centerMode: true,
    // centerPadding: '24vw',
    initialSlide: 1,
  });
};

app.sliderPremiumPlan = function () {
  $(".js-slider-premium-plan").on("init", function (event, slick) {
    let total = slick.slideCount;

    $(this)
      .find(".slick-slide")
      .each(function () {
        let cur = parseInt($(this).data("slickIndex")) + 1;
        $(this)
          .find(".pagi")
          .html("<span>" + cur + "</span><span>" + total + "</span>");
      });

    console.log(slick);
  });

  $(".js-slider-premium-plan").slick({
    slidesToShow: 1,
    slidesToScroll: 1,
    dots: false,
    arrows: true,
    autoplay: true,
    autoplaySpeed: 5000,
    fade: true,
    cssEase: "linear",
  });
};

app.gallerySlider = function () {
  $(".gallery-main").slick({
    slidesToShow: 1,
    slidesToScroll: 1,
    dots: false,
    arrows: false,
    asNavFor: ".gallery-thumb",
  });

  $(".gallery-thumb").slick({
    asNavFor: ".gallery-main",
    slidesToShow: 4,
    slidesToScroll: 1,
    dots: false,
    arrows: false,
    focusOnSelect: true,
  });
};

app.Accordion = function () {
  // let items = document.querySelectorAll(".accordion .accordion-item");

  $(function () {
    var Accordion = function (el, multiple) {
      this.el = el || {};
      this.multiple = multiple || false;

      // Variables privadas
      var links = this.el.find(".accordion-item__head");
      // Evento
      links.on(
        "click",
        { el: this.el, multiple: this.multiple },
        this.dropdown
      );
    };

    Accordion.prototype.dropdown = function (e) {
      var $el = e.data.el;
      ($this = $(this)), ($next = $this.next());

      $next.slideToggle();
      $this.parent().toggleClass("is-open");

      if (!e.data.multiple) {
        $el
          .find(".accordion-item__content")
          .not($next)
          .slideUp()
          .parent()
          .removeClass("is-open");
      }
    };

    var accordion = new Accordion($(".accordion"), false);
  });
};

app.modal = function () {
  $(".modal-go-top").on("click", function (e) {
    e.preventDefault();
    let target = $(this).closest(".modal").find(".modal-body");

    target.animate({ scrollTop: 0 });
  });

  $('[data-bs-dismiss="modal"]').on("click", function (e) {
    e.preventDefault();

    $(this).closest(".modal").removeClass("show").fadeOut(300);
    $(".modal-backdrop").fadeOut(300, function () {
      $(this).remove();
    });
    app.resumeScroll();
  });

  $("body").on("click", ".modal", function (e) {
    if (e.target !== this || e.target.tagName == "A") {
      return;
    }
    e.preventDefault();

    $(this).removeClass("show").fadeOut(300);
    $(".modal-backdrop").fadeOut(300, function () {
      $(this).remove();
    });
    app.resumeScroll();
  });

  $(".show-modal").on("click", function (e) {
    e.preventDefault();
    let modaID = $(this).data("modal");

    $("#" + modaID)
      .fadeIn(300)
      .addClass("show");
    $('<div class="modal-backdrop"></div>').appendTo("body").addClass("show");

    app.stopScroll();
  });
};

app.scrollOnPC = function () {
  const ele = document.getElementById("p-table");

  if (typeof ele != "undefined" && ele != null) {
    ele.style.cursor = "grab";

    let pos = { top: 0, left: 0, x: 0, y: 0 };

    const mouseDownHandler = function (e) {
      ele.style.cursor = "grabbing";
      ele.style.userSelect = "none";

      pos = {
        left: ele.scrollLeft,
        top: ele.scrollTop,
        // Get the current mouse position
        x: e.clientX,
        y: e.clientY,
      };

      document.addEventListener("mousemove", mouseMoveHandler);
      document.addEventListener("mouseup", mouseUpHandler);
    };

    const mouseMoveHandler = function (e) {
      // How far the mouse has been moved
      const dx = e.clientX - pos.x;
      const dy = e.clientY - pos.y;

      // Scroll the element
      ele.scrollTop = pos.top - dy;
      ele.scrollLeft = pos.left - dx;
    };

    const mouseUpHandler = function () {
      ele.style.cursor = "grab";
      ele.style.removeProperty("user-select");

      document.removeEventListener("mousemove", mouseMoveHandler);
      document.removeEventListener("mouseup", mouseUpHandler);
    };

    // Attach the handler
    ele.addEventListener("mousedown", mouseDownHandler);
  }
};

$(document).ready(function () {
  $(".page-top a").click(function () {
    $("html, body").animate({ scrollTop: 0 });
    return false;
  });

  app.init();
});

let path = window.location.pathname;

$(".js-menu-active li a").each(function () {
  let href = $(this).attr("href");
  if (document.location.href.indexOf(href) >= 0) {
    $(this).addClass("is-active");

    if (path != "/" && path != "/kokuyo/") {
      $(".js-menu-active li:first a").removeClass("is-active");
    }
  }
});

function headerAdjust() {
  var adjustPoint = 20;
  var scrollTop = $(window).scrollTop();
  if (scrollTop > adjustPoint) {
    $(".header").addClass("fix");
  } else {
    $(".header").removeClass("fix");
  }
}

function MakeShop_afterListCartEntry(data) {
  if (!data.result) {
    data.method.modal(data.error.message, function () {
      location.href = "/view/item/" + data.systemCode;
    });

    return false;
  }
}

/*はろここを含むurlをactive 現在未使用
if($(location).attr('href').match('hellococo')) {
$('.js-menu-active li a[href*="hellococo"]').addClass('is-active');
}
*/

/*ページ内リンク（FAQと問い合わせ）によるPC右ナビ●ポッチの付与処理を修正*/
/*#contact後方一致で.faqをactive*/
if ($(location).attr("href").match("#contact$")) {
  $(".js-menu-active li a.faq").removeClass("is-active");
}
$(".js-menu-active li a.inq").on("click", function () {
  $(".js-menu-active li a.faq").removeClass("is-active");
  $(".js-menu-active li a.inq").addClass("is-active");
});

//qa_img_popup
const qaModal = document.querySelector(".c-box__box--popup");
const images = document.querySelectorAll(".c-box__img--accordion");
const modalImage = document.querySelector(".c-box__img--popup");

images.forEach(function (image) {
  image.addEventListener("click", function () {
    qaModal.classList.add("show");
    modalImage.classList.add("show");

    var imageSrc = image.getAttribute("src");
    modalImage.src = imageSrc;

    var imageAlt = image.getAttribute("alt");
    modalImage.alt = imageAlt;
  });
});

qaModal.addEventListener("click", function () {
  if (this.classList.contains("show")) {
    this.classList.remove("show");
    modalImage.classList.remove("show");
  }
});
//ポップアップ

let isBannerClosed = false; // バナーが閉じられたかどうかを追跡するフラグ

document.getElementById("closeButton").addEventListener("click", () => {
  const elm = document.getElementById("calloutElm");
  elm.classList.add("fadeOut");
  isBannerClosed = true; // バナーが閉じられたことを記録
});

window.addEventListener("scroll", () => {
  const elm = document.getElementById("calloutElm");
  const scrolledDistance = window.scrollY;
  const screenH = window.innerHeight;

  // バナーがまだ閉じられておらず、かつスクロールが画面幅分以上の場合にのみバナーを表示
  if (!isBannerClosed && scrolledDistance >= screenH) {
    elm.style.display = "block"; // フェードイン前に要素を表示
    elm.classList.remove("fadeOut");
    elm.classList.add("fadeIn");
  } else {
    if (!elm.classList.contains("fadeOut")) {
      elm.classList.add("fadeOut");
    }
  }
});

// フェードアウトアニメーションが終了したときに display: none を適用
document
  .getElementById("calloutElm")
  .addEventListener("animationend", (event) => {
    if (event.animationName === "fadeOut") {
      event.target.style.display = "none";
    }
  });

(function ($) {
  "use strict";
  $.fn.modals = function (option) {
    var o = $.extend(
      {
        target: "#modal",
        width: 500,
        height: 500,
      },
      option
    );
    return this.each(function () {
      if ($(this).get(0) && !$(o.target).get(0)) {
        $("body").append(
          "<div id='modal' class='modal fade' tabindex='-1' role='dialog' aria-labelledby='modal' aria-hidden='true'><div class='modal-dialog modal-dialog-centered' role='document'><div class='modal-content'/></div></div>"
        );
        $("body").append(
          "<button type='button' id='modal-close' class='modal-close' data-dismiss='modal'/>"
        );
      }
      $(document).on("click", "[data-dismiss=modal]", function (event) {
        if (!$(o.target).get(0)) {
          $("#modal-close", window.parent.document).trigger("click");
        } else {
          $(o.target).modal("hide");
        }
      });
      $(document).on("click", "[data-toggle=modal]", function (event) {
        // eventじゃなくthisで取得できる
        // var a = $(event.target).attr("href");
        var a = $(this).attr("href");
        if (typeof a !== "undefined" && a !== false) {
          var b = $(o.target).find(".modal-content");
          b.empty();
          b.append(
            "<iframe src='" +
              a +
              "' class='modal-frame' width='" +
              o.width +
              "' height='" +
              o.height +
              "'></iframe>"
          );
          $(o.target).modal();
        }
      });
    });
  };
})(jQuery);
