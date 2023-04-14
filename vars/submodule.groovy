#!/usr/bin/env groovy

def submodule()
{
    sh 'git submodule deinit -f .'
    sh 'git submodule update --init --remote --merge'
}