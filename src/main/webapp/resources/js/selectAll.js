function selectAll() {
    const checkbox = document.getElementsByClassName('masterMark')[0];

    const elements = document.getElementsByClassName('mark');

    for (let element of elements) {
        element.checked = checkbox.checked
    }
}

function confirmSelection(msg) {

    const markedElements = document.querySelectorAll('.mark:checked').length

    if (markedElements === 0) return false

    return confirm(msg)
}
