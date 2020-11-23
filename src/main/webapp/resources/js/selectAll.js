function selectAll() {
    const checkbox = document.getElementsByClassName('masterMark')[0];

    const elements = document.getElementsByClassName('mark');

    for (let element of elements) {
        element.checked = checkbox.checked
    }
}
