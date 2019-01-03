import axios from 'axios'
import Book from '../Domain;
export default class BookService {

    async getByIsbn(isbn) {
        const volume = await axios.get('/https://www.googleapis.com/books/v1/volumes?q=isbn:' + isbn);
        if(volume.items) {
            const result = []
            for (var i = 0; i < response.items.length; i++) {
              var item = response.items[i];
              const info = item.volumeInfo
              info.industryIdentifiers.
              result.push(new Book())
            }
            return result
        } else {
            return null
        }
    }
}