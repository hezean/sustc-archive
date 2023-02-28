function keyMirror(obj) {
  if (obj instanceof Object) {
    let _obj = Object.assign({}, obj);
    let _keyArray = Object.keys(obj);
    _keyArray.forEach(key => {
      _obj[key] = key;
    });
    return _obj;
  }
}

export default keyMirror({
  CHANGE_LANGUAGE: 0,
});
