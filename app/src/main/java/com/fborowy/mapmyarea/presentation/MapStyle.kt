package com.fborowy.mapmyarea.presentation

object MapStyle {

    // Assassin's Creed IV map style by Sarah Frisk
//    val mapStyleJson = """
//        [
//            {
//                "featureType": "all",
//                "elementType": "all",
//                "stylers": [
//                    {
//                        "visibility": "on"
//                    }
//                ]
//            },
//            {
//                "featureType": "all",
//                "elementType": "labels",
//                "stylers": [
//                    {
//                        "visibility": "on"
//                    },
//                    {
//                        "saturation": "36"
//                    }
//                ]
//            },
//            {
//                "featureType": "all",
//                "elementType": "labels.text.fill",
//                "stylers": [
//                    {
//                        "saturation": 36
//                    },
//                    {
//                        "color": "#000000"
//                    },
//                    {
//                        "lightness": 40
//                    },
//                    {
//                        "visibility": "on"
//                    }
//                ]
//            },
//            {
//                "featureType": "all",
//                "elementType": "labels.text.stroke",
//                "stylers": [
//                    {
//                        "visibility": "on"
//                    },
//                    {
//                        "color": "#000000"
//                    },
//                    {
//                        "lightness": 16
//                    }
//                ]
//            },
//            {
//                "featureType": "all",
//                "elementType": "labels.icon",
//                "stylers": [
//                    {
//                        "visibility": "on"
//                    }
//                ]
//            },
//            {
//                "featureType": "administrative",
//                "elementType": "geometry.fill",
//                "stylers": [
//                    {
//                        "color": "#000000"
//                    },
//                    {
//                        "lightness": 20
//                    }
//                ]
//            },
//            {
//                "featureType": "administrative",
//                "elementType": "geometry.stroke",
//                "stylers": [
//                    {
//                        "color": "#000000"
//                    },
//                    {
//                        "lightness": 17
//                    },
//                    {
//                        "weight": 1.2
//                    }
//                ]
//            },
//            {
//                "featureType": "landscape",
//                "elementType": "geometry",
//                "stylers": [
//                    {
//                        "color": "#000000"
//                    },
//                    {
//                        "lightness": 20
//                    }
//                ]
//            },
//            {
//                "featureType": "landscape",
//                "elementType": "geometry.fill",
//                "stylers": [
//                    {
//                        "color": "#4d6059"
//                    }
//                ]
//            },
//            {
//                "featureType": "landscape",
//                "elementType": "geometry.stroke",
//                "stylers": [
//                    {
//                        "color": "#4d6059"
//                    }
//                ]
//            },
//            {
//                "featureType": "landscape.natural",
//                "elementType": "geometry.fill",
//                "stylers": [
//                    {
//                        "color": "#4d6059"
//                    }
//                ]
//            },
//            {
//                "featureType": "poi",
//                "elementType": "geometry",
//                "stylers": [
//                    {
//                        "lightness": 21
//                    }
//                ]
//            },
//            {
//                "featureType": "poi",
//                "elementType": "geometry.fill",
//                "stylers": [
//                    {
//                        "color": "#4d6059"
//                    }
//                ]
//            },
//            {
//                "featureType": "poi",
//                "elementType": "geometry.stroke",
//                "stylers": [
//                    {
//                        "color": "#4d6059"
//                    }
//                ]
//            },
//            {
//                "featureType": "poi",
//                "elementType": "all",
//                "stylers": [
//                    {
//                        "visibility": "off"
//                    }
//                ]
//            },
//            {
//                "featureType": "road",
//                "elementType": "geometry",
//                "stylers": [
//                    {
//                        "visibility": "on"
//                    },
//                    {
//                        "color": "#7f8d89"
//                    }
//                ]
//            },
//            {
//                "featureType": "road",
//                "elementType": "geometry.fill",
//                "stylers": [
//                    {
//                        "color": "#7f8d89"
//                    }
//                ]
//            },
//            {
//                "featureType": "road.highway",
//                "elementType": "geometry.fill",
//                "stylers": [
//                    {
//                        "color": "#7f8d89"
//                    },
//                    {
//                        "lightness": 17
//                    }
//                ]
//            },
//            {
//                "featureType": "road.highway",
//                "elementType": "geometry.stroke",
//                "stylers": [
//                    {
//                        "color": "#7f8d89"
//                    },
//                    {
//                        "lightness": 29
//                    },
//                    {
//                        "weight": 0.2
//                    }
//                ]
//            },
//            {
//                "featureType": "road.arterial",
//                "elementType": "geometry",
//                "stylers": [
//                    {
//                        "color": "#000000"
//                    },
//                    {
//                        "lightness": 18
//                    }
//                ]
//            },
//            {
//                "featureType": "road.arterial",
//                "elementType": "geometry.fill",
//                "stylers": [
//                    {
//                        "color": "#7f8d89"
//                    }
//                ]
//            },
//            {
//                "featureType": "road.arterial",
//                "elementType": "geometry.stroke",
//                "stylers": [
//                    {
//                        "color": "#7f8d89"
//                    }
//                ]
//            },
//            {
//                "featureType": "road.local",
//                "elementType": "geometry",
//                "stylers": [
//                    {
//                        "color": "#000000"
//                    },
//                    {
//                        "lightness": 16
//                    }
//                ]
//            },
//            {
//                "featureType": "road.local",
//                "elementType": "geometry.fill",
//                "stylers": [
//                    {
//                        "color": "#7f8d89"
//                    }
//                ]
//            },
//            {
//                "featureType": "road.local",
//                "elementType": "geometry.stroke",
//                "stylers": [
//                    {
//                        "color": "#7f8d89"
//                    }
//                ]
//            },
//            {
//                "featureType": "transit",
//                "elementType": "geometry",
//                "stylers": [
//                    {
//                        "color": "#000000"
//                    },
//                    {
//                        "lightness": 19
//                    }
//                ]
//            },
//            {
//                "featureType": "water",
//                "elementType": "all",
//                "stylers": [
//                    {
//                        "color": "#2b3638"
//                    },
//                    {
//                        "visibility": "on"
//                    }
//                ]
//            },
//            {
//                "featureType": "water",
//                "elementType": "geometry",
//                "stylers": [
//                    {
//                        "color": "#2b3638"
//                    },
//                    {
//                        "lightness": 17
//                    }
//                ]
//            },
//            {
//                "featureType": "water",
//                "elementType": "geometry.fill",
//                "stylers": [
//                    {
//                        "color": "#24282b"
//                    }
//                ]
//            },
//            {
//                "featureType": "water",
//                "elementType": "geometry.stroke",
//                "stylers": [
//                    {
//                        "color": "#24282b"
//                    }
//                ]
//            },
//            {
//                "featureType": "water",
//                "elementType": "labels",
//                "stylers": [
//                    {
//                        "visibility": "on"
//                    }
//                ]
//            },
//            {
//                "featureType": "water",
//                "elementType": "labels.text",
//                "stylers": [
//                    {
//                        "visibility": "on"
//                    }
//                ]
//            },
//            {
//                "featureType": "water",
//                "elementType": "labels.text.fill",
//                "stylers": [
//                    {
//                        "visibility": "on"
//                    }
//                ]
//            },
//            {
//                "featureType": "water",
//                "elementType": "labels.text.stroke",
//                "stylers": [
//                    {
//                        "visibility": "on"
//                    }
//                ]
//            },
//            {
//                "featureType": "water",
//                "elementType": "labels.icon",
//                "stylers": [
//                    {
//                        "visibility": "on"
//                    }
//                ]
//            }
//        ]
//    """.trimIndent()


    //micha https://snazzymaps.com/style/287008/me-and-all
    val mapStyleJson = """
        [
            {
                "featureType": "all",
                "elementType": "all",
                "stylers": [
                    {
                        "visibility": "on"
                    }
                ]
            },
            {
                "featureType": "all",
                "elementType": "geometry",
                "stylers": [
                    {
                        "visibility": "on"
                    }
                ]
            },
            {
                "featureType": "all",
                "elementType": "labels.text.fill",
                "stylers": [
                    {
                        "saturation": 36
                    },
                    {
                        "color": "#000000"
                    },
                    {
                        "lightness": 40
                    }
                ]
            },
            {
                "featureType": "all",
                "elementType": "labels.text.stroke",
                "stylers": [
                    {
                        "visibility": "on"
                    },
                    {
                        "color": "#000000"
                    },
                    {
                        "lightness": 16
                    }
                ]
            },
            {
                "featureType": "all",
                "elementType": "labels.icon",
                "stylers": [
                    {
                        "visibility": "off"
                    }
                ]
            },
            {
                "featureType": "administrative",
                "elementType": "geometry.stroke",
                "stylers": [
                    {
                        "color": "#000000"
                    },
                    {
                        "lightness": 17
                    },
                    {
                        "weight": 1.2
                    }
                ]
            },
            {
                "featureType": "administrative.country",
                "elementType": "geometry",
                "stylers": [
                    {
                        "color": "#868686"
                    },
                    {
                        "visibility": "off"
                    }
                ]
            },
            {
                "featureType": "administrative.country",
                "elementType": "geometry.stroke",
                "stylers": [
                    {
                        "visibility": "on"
                    }
                ]
            },
            {
                "featureType": "administrative.country",
                "elementType": "labels.text.fill",
                "stylers": [
                    {
                        "color": "#d2d2d2"
                    }
                ]
            },
            {
                "featureType": "administrative.province",
                "elementType": "geometry",
                "stylers": [
                    {
                        "color": "#676767"
                    },
                    {
                        "visibility": "on"
                    }
                ]
            },
            {
                "featureType": "administrative.locality",
                "elementType": "labels.text.fill",
                "stylers": [
                    {
                        "color": "#848484"
                    }
                ]
            },
            {
                "featureType": "landscape",
                "elementType": "geometry",
                "stylers": [
                    {
                        "color": "#000000"
                    },
                    {
                        "lightness": 20
                    }
                ]
            },
            {
                "featureType": "landscape",
                "elementType": "geometry.fill",
                "stylers": [
                    {
                        "color": "#212121"
                    },
                    {
                        "visibility": "on"
                    }
                ]
            },
            {
                "featureType": "landscape.man_made",
                "elementType": "geometry.fill",
                "stylers": [
                    {
                        "visibility": "on"
                    },
                    {
                        "color": "#212121"
                    }
                ]
            },
            {
                "featureType": "landscape.natural",
                "elementType": "geometry.fill",
                "stylers": [
                    {
                        "visibility": "on"
                    },
                    {
                        "color": "#212121"
                    }
                ]
            },
            {
                "featureType": "landscape.natural.landcover",
                "elementType": "geometry.fill",
                "stylers": [
                    {
                        "color": "#212121"
                    },
                    {
                        "visibility": "on"
                    }
                ]
            },
            {
                "featureType": "landscape.natural.terrain",
                "elementType": "geometry.fill",
                "stylers": [
                    {
                        "color": "#212121"
                    },
                    {
                        "visibility": "on"
                    }
                ]
            },
            {
                "featureType": "poi",
                "elementType": "geometry",
                "stylers": [
                    {
                        "lightness": 21
                    },
                    {
                        "color": "#212121"
                    }
                ]
            },
            {
                "featureType": "poi.park",
                "elementType": "geometry",
                "stylers": [
                    {
                        "color": "#181818"
                    }
                ]
            },
            {
                "featureType": "road",
                "elementType": "geometry",
                "stylers": [
                    {
                        "visibility": "simplified"
                    }
                ]
            },
            {
                "featureType": "road.highway",
                "elementType": "geometry",
                "stylers": [
                    {
                        "visibility": "simplified"
                    },
                    {
                        "color": "#3c3c3c"
                    }
                ]
            },
            {
                "featureType": "road.highway",
                "elementType": "geometry.stroke",
                "stylers": [
                    {
                        "lightness": 29
                    },
                    {
                        "weight": 0.2
                    }
                ]
            },
            {
                "featureType": "road.highway.controlled_access",
                "elementType": "geometry",
                "stylers": [
                    {
                        "color": "#4e4e4e"
                    },
                    {
                        "visibility": "simplified"
                    },
                    {
                        "lightness": "-20"
                    }
                ]
            },
            {
                "featureType": "road.arterial",
                "elementType": "geometry",
                "stylers": [
                    {
                        "visibility": "simplified"
                    },
                    {
                        "color": "#373737"
                    }
                ]
            },
            {
                "featureType": "road.arterial",
                "elementType": "geometry.fill",
                "stylers": [
                    {
                        "visibility": "on"
                    }
                ]
            },
            {
                "featureType": "road.local",
                "elementType": "geometry",
                "stylers": [
                    {
                        "lightness": 16
                    },
                    {
                        "color": "#313131"
                    },
                    {
                        "visibility": "simplified"
                    }
                ]
            },
            {
                "featureType": "transit",
                "elementType": "geometry",
                "stylers": [
                    {
                        "color": "#212121"
                    },
                    {
                        "lightness": 19
                    },
                    {
                        "visibility": "off"
                    }
                ]
            },
            {
                "featureType": "water",
                "elementType": "geometry",
                "stylers": [
                    {
                        "color": "#000000"
                    },
                    {
                        "visibility": "on"
                    }
                ]
            }
        ]
    """.trimIndent()
}