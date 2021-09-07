package com.charlezz.data.flickr

/**
 * "photos": {
        "page": 1,
        "pages": 1154,
        "perpage": 100,
        "total": 115323,
        "photo": [
            {
                "id": "51405312918",
                "owner": "153499399@N02",
                "secret": "b11c94e3d9",
                "server": "65535",
                "farm": 66,
                "title": "Dollar Tree-Spirit Lake, Iowa",
                "ispublic": 1,
                "isfriend": 0,
                "isfamily": 0
            }, ...
        ]
    },
    "stat": "ok"
 */
data class FlickrResult(
    val photos:FlickrPhotos,
    val stat: String,
)