
// Load the data for a specific category, based on
// the URL passed in. Generate markup for the items in the
// category, inject it into an embedded page, and then make
// that page the current active page.

function showCategory( urlObj, options ) {

    var categoryName = urlObj.hash.replace( /.*category=/, "" ),
    // Get the object that represents the category we
    // are interested in. Note, that at this point we could
    // instead fire off an ajax request to fetch the data, but
    // for the purposes of this sample, it's already in memory.
    category = categoryData[ categoryName ],
    // The pages we use to display our content are already in
    // the DOM. The id of the page we are going to write our
    // content into is specified in the hash before the '?'.
    pageSelector = urlObj.hash.replace( /\?.*$/, "" );

    if ( category ) {
	// Get the page we are going to dump our content into.
	var $page = $( pageSelector ),

	// Get the header for the page.
	$header = $page.children( ":jqmData(role=header)" ),

	// Get the content area element for the page.
	$content = $page.children( ":jqmData(role=content)" ),

	// The markup we are going to inject into the content
	// area of the page.
	markup = "<p>" + category.description + "</p><ul data-role='listview' data-inset='true'>",

	// The array of items for this category.
	cItems = category.items,

	// The number of items in the category.
	numItems = cItems.length;

	// Generate a list item for each item in the category
	// and add it to our markup.
	for ( var i = 0; i < numItems; i++ ) {
	    markup += "<li>" + cItems[i].name + "</li>";
	}
	markup += "</ul>";

	// Find the h1 element in our header and inject the name of
	// the category into it.
	$header.find( "h1" ).html( category.name );

	// Inject the category items markup into the content element.
	$content.html( markup );

	// Pages are lazily enhanced. We call page() on the page
	// element to make sure it is always enhanced before we
	// attempt to enhance the listview markup we just injected.
	// Subsequent calls to page() are ignored since a page/widget
	// can only be enhanced once.
	$page.page();

	// Enhance the listview we just injected.
	$content.find( ":jqmData(role=listview)" ).listview();

	// We don't want the data-url of the page we just modified
	// to be the url that shows up in the browser's location field,
	// so set the dataUrl option to the URL for the category
	// we just loaded.
	options.dataUrl = urlObj.href;

	// Now call changePage() and tell it to switch to
	// the page we just modified.
	$.mobile.changePage( $page, options );
    }
}