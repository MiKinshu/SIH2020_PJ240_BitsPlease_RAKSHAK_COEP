var mongoose = require("mongoose");
var NetworkSchema = new mongoose.Schema({
  name:{
    type: String,
    default:""
  },
  password: {
    type: String,
    default: ""
  },
  networkId:{
    type: String,
    default: ""
  },
  users:{
    type: [String],
    default: []
  }
});
mongoose.model("Network", NetworkSchema);

module.exports = mongoose.model("Network");
