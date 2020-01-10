(function() {

    /**
     * Variables
     */
    var user_id = '1111';
    var user_fullname = 'John';
    var lng = -122.08;
    var lat = 37.38;

    /**
     * Initialize   定义functions
     */
    function init() {
        // Register event listeners
    	//$ = document.getElementById();
        $('nearby-btn').addEventListener('click', loadNearbyItems); //trigger function
        $('fav-btn').addEventListener('click', loadFavoriteItems);
        $('recommend-btn').addEventListener('click', loadRecommendedItems);

        var welcomeMsg = $('welcome-msg');     //先找到welcome message之和inner HTML加上name，之后name来自后台
        welcomeMsg.innerHTML = 'Welcome, ' + user_fullname; 
        loadNearbyItems();        //触发nearby items
    }
    
    function loadNearbyItems() {
    	activeBtn('nearby-btn');   
    	var nearbyItems = mockSearchResponse;
    	listItems(nearbyItems);
    }
    
    function loadFavoriteItems() {
    	activeBtn('fav-btn');
    	var favoriteItems = mockSearchResponse.slice(3, 6);
    	listItems(favoriteItems);
    }
    
    function loadRecommendedItems() {
    	activeBtn('recommend-btn');
    	var recommendedItems = mockSearchResponse.slice(10);    //取第十个item之后的
    	listItems(recommendedItems);
    }

    // -----------------------------------
    // Helper Functions
    // -----------------------------------

    /**
     * A helper function that makes a navigation button active
     * 
     * @param btnId -
     *            The id of the navigation button
     */
    function activeBtn(btnId) {
        var btns = document.getElementsByClassName('main-nav-btn'); //选中三个button

        // deactivate all navigation buttons
        for (var i = 0; i < btns.length; i++) {
           btns[i].className = btns[i].className.replace(/\bactive\b/, ''); //   regular expression？ 将未被选中的active替换为空
        }

        // active the one that has id = btnId
        var btn = $(btnId); 
        btn.className += ' active'; //将选中的设为active
    }

    function showLoadingMessage(msg) {
        var itemList = $('item-list');
        itemList.innerHTML = '<p class="notice"><i class="fa fa-spinner fa-spin"></i> ' +
            msg + '</p>';
    }

    function showWarningMessage(msg) {
        var itemList = $('item-list');
        itemList.innerHTML = '<p class="notice"><i class="fa fa-exclamation-triangle"></i> ' +
            msg + '</p>';
    }

    function showErrorMessage(msg) {
        var itemList = $('item-list');
        itemList.innerHTML = '<p class="notice"><i class="fa fa-exclamation-circle"></i> ' +
            msg + '</p>';
    }

    /**
     * A helper function that creates a DOM element <tag options...>
     * 
     * @param tag
     * @param options
     * @returns
     */
    function $(tag, options) {
        if (!options) {
            return document.getElementById(tag);  //如不给options就是get element by id
        }

        var element = document.createElement(tag);  //创建element

        for (var option in options) {   //设定attribute
            if (options.hasOwnProperty(option)) { // 不是继承而来的
                element[option] = options[option];  
            }
        }

        return element;
    }

    function hideElement(element) {
        element.style.display = 'none';
    }

    function showElement(element, style) {
        var displayStyle = style ? style : 'block';
        element.style.display = displayStyle;
    }

    // -------------------------------------
    // Create item list
    // -------------------------------------

    /**
     * List items
     * 
     * @param items -
     *            An array of item JSON objects
     */
    function listItems(items) {           //把每个item写入list
        // Clear the current results
        var itemList = $('item-list');
        itemList.innerHTML = '';    //清空整个list

        for (var i = 0; i < items.length; i++) {
            addItem(itemList, items[i]);   //加入每个item
        }
    }

    /**
     * Add item to the list
     * 
     * @param itemList -
     *            The
     *            <ul id="item-list">
     *            tag
     * @param item -
     *            The item data (JSON object)
     */
    function addItem(itemList, item) {
        var item_id = item.item_id;

        // create the <li> tag and specify the id and class attributes
        var li = $('li', {  
            id: 'item-' + item_id,
            className: 'item'
        });                             //创建一个li 
        
        //设定attributes

        // set the data attribute
        li.dataset.item_id = item_id;
        li.dataset.favorite = item.favorite;

        // item image
        if (item.image_url) {
            li.appendChild($('img', {
                src: item.image_url
            }));
        } else {
            li.appendChild($(
                'img', {
                    src: 'https://assets-cdn.github.com/images/modules/logos_page/GitHub-Mark.png'
                }))    
                //如果有回传image就append，没有就default image
        }
        // section
        var section = $('div', {});

        // title
        var title = $('a', {
            href: item.url,
            target: '_blank',
            className: 'item-name'
        });
        title.innerHTML = item.name;
        section.appendChild(title);

        // category
        var category = $('p', {
            className: 'item-category'
        });
        category.innerHTML = 'Category: ' + item.categories.join(', ');
        section.appendChild(category);

        var stars = $('div', {
            className: 'stars'
        });

        for (var i = 0; i < item.rating; i++) {
            var star = $('i', {
                className: 'fa fa-star'
            });
            stars.appendChild(star);
        }

        if (('' + item.rating).match(/\.5$/)) {
            stars.appendChild($('i', {
                className: 'fa fa-star-half-o'
            }));
        }

        section.appendChild(stars);

        li.appendChild(section);

        // address
        var address = $('p', {
            className: 'item-address'
        });

        address.innerHTML = item.address.replace(/,/g, '<br/>').replace(/\"/g, '');
        li.appendChild(address);

        // favorite link
        var favLink = $('p', {
            className: 'fav-link'
        });

        favLink.appendChild($('i', {
            id: 'fav-icon-' + item_id,
            className: item.favorite ? 'fa fa-heart' : 'fa fa-heart-o'
        }));

        li.appendChild(favLink);

        itemList.appendChild(li);
    }
    
    init();   //执行init
    
})();