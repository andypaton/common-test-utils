#!/bin/bash

( LD_LIBRARY_PATH=/opt/google/chrome/lib:/opt/google/chrome $PWD/src/test/resources/chrome_driver/chromedriver ${@} )
